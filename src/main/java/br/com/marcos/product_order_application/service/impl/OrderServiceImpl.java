package br.com.marcos.product_order_application.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import br.com.marcos.product_order_infrastructure.metrics.OrderMetrics;
import br.com.marcos.product_order_infrastructure.repository.OrderItemRepository;
import br.com.marcos.product_order_infrastructure.repository.OrderRepository;
import br.com.marcos.product_order_infrastructure.repository.OutboxEventRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import br.com.marcos.product_order_infrastructure.repository.UserRepository;
import br.com.marcos.product_order_infrastructure.security.SecurityUtils;
import io.micrometer.core.annotation.Timed;

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

    /* =========================
       CREATE ORDER
       ========================= */

    @Override
    @PreAuthorize("hasRole('USER')")
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {

         log.info("Creating order for userAccountId={}", request.getUserAccountId());

         User user = userRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // ✅ Criação correta do agregado Order
        Order order = new Order(
               user.getId(),
               request.getUserAccountId()
        );

        for (OrderItemRequestDTO itemRequest : request.getItems()) {

           Product product = productRepository.findById(itemRequest.getProductId())
                   .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

           if (product.getStockQuantity() < itemRequest.getQuantity()) {
                 throw new BusinessException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal subtotal = product.getPrice()
                   .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

           OrderItem item = new OrderItem();
           item.setProductId(product.getId());
           item.setProductNameSnapshot(product.getName());
           item.setUnitPriceSnapshot(product.getPrice());
           item.setQuantity(itemRequest.getQuantity());
           item.setSubtotal(subtotal);

        // ✅ regra de domínio
           order.addItem(item);
        }

        Order savedOrder = orderRepository.save(order);
        orderMetrics.incrementOrderCreated();

        log.info("Order created id={} total={}", savedOrder.getId(), savedOrder.getTotal());

        return toResponse(savedOrder);
        }

    /* =========================
       PAYMENT
       ========================= */

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

/* =========================
       STOCK UPDATE
       ========================= */

    @Override // Certifique-se de que este @Override está aqui
    @Transactional
    public void updateStock(UUID orderId) {
        // Agora o método updateStock (da interface) chama o seu updateStockOrder
        updateStockOrder(orderId);
    }

    @Transactional // Remova o @Override daqui, pois este nome NÃO está na interface
    public void updateStockOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.isStockUpdated()) {
            return;
        }

        // Importante: Para os testes passarem, troque BusinessException por IllegalStateException
        // e ajuste a mensagem conforme os testes esperam.
        if (order.getStatus() != OrderStatus.PAGO) {
            throw new IllegalStateException("Pedido não está pago");
        }

        List<OrderItem> items = itemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            int newStock = product.getStockQuantity() - item.getQuantity();

            if (newStock < 0) {
                throw new IllegalStateException("Estoque insuficiente");
            }

            product.setStockQuantity(newStock);
            productRepository.save(product);
        }

        order.markStockUpdated();
        orderRepository.save(order);
    }

    /* =========================
       OUTBOX
       ========================= */

    private void createOutboxEvent(Order order) {

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", order.getId());
            payload.put("paidAt", Instant.now().toString());

            OutboxEvent event = new OutboxEvent();
            event.setAggregateType("Order");
            event.setAggregateId(order.getId().toString());
            event.setEventType("order.paid");
            event.setPayload(objectMapper.writeValueAsString(payload));
            event.setStatus("PENDENTE");
            event.setCreatedAt(Instant.now());

            outboxRepository.save(event);

        } catch (Exception e) {
            log.error("Error creating outbox event", e);
            throw new RuntimeException("Failed to create outbox event", e);
        }
    }

    /* =========================
       MAPPER
       ========================= */

    private OrderResponseDTO toResponse(Order order) {

        List<OrderItemResponseDTO> items = order.getItems().stream()
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
                items
        );
    }
}
