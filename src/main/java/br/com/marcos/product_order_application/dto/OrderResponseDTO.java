package br.com.marcos.product_order_application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.marcos.product_order_domain.enums.OrderStatus;

public class OrderResponseDTO {

    private UUID id;
    private OrderStatus status;
    private BigDecimal total;
    private Instant createdAt;
    private List<OrderItemResponseDTO> items;

    public OrderResponseDTO(
            UUID id,
            OrderStatus status,
            BigDecimal total,
            Instant createdAt,
            List<OrderItemResponseDTO> items
    ) {
        this.id = id;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.items = items;
    }

    public UUID getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }
}
