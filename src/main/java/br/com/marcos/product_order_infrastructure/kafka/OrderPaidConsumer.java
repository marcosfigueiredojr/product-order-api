package br.com.marcos.product_order_infrastructure.kafka;

import br.com.marcos.product_order_domain.entity.Order;
import br.com.marcos.product_order_domain.entity.OrderItem;
import br.com.marcos.product_order_domain.exceptions.ResourceNotFoundException;
import br.com.marcos.product_order_infrastructure.repository.OrderItemRepository;
import br.com.marcos.product_order_infrastructure.repository.OrderRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderPaidConsumer {

    private final OrderRepository orderRepository;
    private final OrderItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderPaidConsumer(
            OrderRepository orderRepository,
            OrderItemRepository itemRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
    }

    @KafkaListener(topics = "order.paid", groupId = "order-stock-group")
    @Transactional
    public void consume(String message) {

        try {
            @SuppressWarnings("unchecked")
			Map<String, Object> payload =
                    objectMapper.readValue(message, Map.class);

            UUID orderId = UUID.fromString(payload.get("orderId").toString());

            @SuppressWarnings("unused")
			Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            List<OrderItem> items = itemRepository.findByOrderId(orderId);

            for (OrderItem item : items) {
                productRepository.findById(item.getProductId())
                        .ifPresent(product -> {
                            product.setStockQuantity(
                                    product.getStockQuantity() - item.getQuantity()
                            );
                            productRepository.save(product);
                        });
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to process order.paid event");
        }
    }
}
