package br.com.marcos.product_order_application.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.marcos.product_order_application.dto.CreateOrderRequestDTO;
import br.com.marcos.product_order_application.dto.OrderItemRequestDTO;
import br.com.marcos.product_order_application.dto.OrderItemResponseDTO;
import br.com.marcos.product_order_application.dto.OrderResponseDTO;
import br.com.marcos.product_order_application.service.OrderService;
import br.com.marcos.product_order_domain.entity.Order;
import br.com.marcos.product_order_domain.entity.OrderItem;
import br.com.marcos.product_order_domain.entity.OutboxEvent;
import br.com.marcos.product_order_domain.entity.Product;
import br.com.marcos.product_order_domain.entity.User;
import br.com.marcos.product_order_domain.enums.OrderStatus;
import br.com.marcos.product_order_domain.exceptions.BusinessException;
import br.com.marcos.product_order_domain.exceptions.ResourceNotFoundException;
import br.com.marcos.product_order_infrastructure.repository.OrderItemRepository;
import br.com.marcos.product_order_infrastructure.repository.OrderRepository;
import br.com.marcos.product_order_infrastructure.repository.OutboxEventRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import br.com.marcos.product_order_infrastructure.repository.UserRepository;
import io.micrometer.core.annotation.Timed;
import br.com.marcos.product_order_infrastructure.metrics.OrderMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.com.marcos.product_order_infrastructure.security.SecurityUtils;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OutboxEventRepository outboxRepository;
    private final OrderMetrics orderMetrics;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository itemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            OutboxEventRepository outboxRepository,
            OrderMetrics orderMetrics
    ) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.outboxRepository = outboxRepository;
        this.orderMetrics = orderMetrics;
    }
    

    @Override
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {

        log.info("Creating order for userAccountId={}", request.getUserAccountId());
        User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUserId(request.getUserAccountId());
        order.setUserAccountId(request.getUserAccountId());
        order.setStatus(OrderStatus.PENDENTE);
        order.setCreatedAt(Instant.now());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequestDTO itemRequest : request.getItems()) {

            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                log.warn("Stock insufficient for product: {}", product.getName());
                order.setStatus(OrderStatus.CANCELADO);
                orderRepository.save(order);
                throw new BusinessException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductNameSnapshot(product.getName());
            item.setUnitPriceSnapshot(product.getPrice());
            item.setQuantity(itemRequest.getQuantity());
            item.setSubtotal(subtotal);

            items.add(item);
            total = total.add(subtotal);
        }

        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);
        itemRepository.saveAll(items);
        orderMetrics.incrementOrderCreated();
        log.info("Order created id={} total={}", savedOrder.getId(), savedOrder.getTotal());

        return toResponse(savedOrder, items);
    }

    @Override
    @Transactional
    public void updateStockOrder(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.isStockUpdated()) {
            return;
        }

        if (order.getStatus() != OrderStatus.PAGO) {
            throw new BusinessException("Order must be PAID to update stock");
        }

        List<OrderItem> items = itemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            int newStock = product.getStockQuantity() - item.getQuantity();

            if (newStock < 0) {
                throw new BusinessException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(newStock);
            productRepository.save(product);
        }

        order.setStockUpdated(true);
        orderRepository.save(order);
    }

    
    @Override
    @PreAuthorize("hasRole('USER')")
    @Timed(value = "order.payment.time", histogram = true)
    public void payOrder(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDENTE) {
            throw new BusinessException("Only pending orders can be paid");
        }

        order.setStatus(OrderStatus.PAGO);
        orderRepository.save(order);

        createOutboxEvent(order);
    }

    /**
     * Este método é chamado pelo OrderPaidConsumer após o evento de pagamento.
     * Ele localiza o pedido e processa a baixa de todos os itens vendidos.
     */
    @Override
    @Transactional
    public void updateStock(UUID orderId) {
        // Busca o pedido para garantir que ele existe
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found for ID: " + orderId));

        // Reutiliza a lógica de idempotência e baixa de itens do updateStockOrder
        // para manter o comportamento consistente no sistema
        updateStockOrder(orderId);
    }
    /* =========================
       OUTBOX
       ========================= */

    private void createOutboxEvent(Order order) {
        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("orderId", order.getId());
            payloadMap.put("paidAt", Instant.now().toString());

            OutboxEvent event = new OutboxEvent();
          
            event.setAggregateType("Order");
            event.setAggregateId(order.getId().toString()); // Convertendo UUID do pedido para String
            event.setEventType("order.paid");
            event.setPayload(objectMapper.writeValueAsString(payloadMap));
            event.setStatus("PENDENTE");
            
           event.setCreatedAt(Instant.now());

            outboxRepository.save(event);

        } catch (Exception e) {
            // Log do erro real no console para você saber o que falhou (importante!)
            System.err.println("Erro detalhado ao criar Outbox: " + e.getMessage());
            throw new RuntimeException("Failed to create outbox event: " + e.getMessage());
        }
    }

    /* =========================
       MAPPER
       ========================= */

    private OrderResponseDTO toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponseDTO> itemResponses = items.stream()
                .map(i -> new OrderItemResponseDTO(
                        i.getProductId(),
                        i.getProductNameSnapshot(),
                        i.getQuantity(),
                        i.getUnitPriceSnapshot(),
                        i.getSubtotal()
                ))
                .toList();

        return new OrderResponseDTO(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}