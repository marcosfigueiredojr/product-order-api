package br.com.marcos.product_order_infrastructure.messaging.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.marcos.product_order_application.service.OrderService;

@Component
public class OrderPaidConsumer {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderPaidConsumer(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "order.paid",
        groupId = "stock-service"
    )
    public void onMessage(String message) {
        try {
            var json = objectMapper.readTree(message);
            UUID orderId = UUID.fromString(json.get("orderId").asText());

            orderService.updateStock(orderId);

        } catch (Exception e) {
            // log.error("Failed to process order.paid", e);
            throw new RuntimeException(e);
        }
    }
}
