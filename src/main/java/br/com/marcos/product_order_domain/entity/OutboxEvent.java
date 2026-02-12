package br.com.marcos.product_order_domain.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_outbox_event")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Alinhado com o AUTO_INCREMENT do MySQL
    private Long id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId; // Alterado para String para evitar conflitos de conversão BINARY

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(columnDefinition = "LONGTEXT", nullable = false) // LONGTEXT garante que caiba o JSON
    private String payload;

    @Column(nullable = false, length = 50) // Aumentado para 50 caracteres conforme seu banco
    private String status; 

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public OutboxEvent() {}

    public OutboxEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload,
            String status
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
    }

    @PrePersist
    protected void prePersist() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = "PENDENTE";
        }
    }

    /* Getters and Setters Atualizados */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}