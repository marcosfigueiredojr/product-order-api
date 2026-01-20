package br.com.marcos.product_order_domain.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_outbox_event")
public class OutboxEvent {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 100)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType; // order.paid, product.created, etc

    @Column(columnDefinition = "JSON", nullable = false)
    private String payload;

    @Column(nullable = false, length = 20)
    private String status; // PENDING | SENT | ERROR

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    /* =========================
       Constructors
       ========================= */

    public OutboxEvent() {
        // JPA default constructor
    }

    public OutboxEvent(
            String aggregateType,
            UUID aggregateId,
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

    /* =========================
       JPA callbacks
       ========================= */

    @PrePersist
    protected void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    /* =========================
       Getters and Setters
       ========================= */

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(UUID aggregateId) {
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

	public void setAggregateId(String string) {
		// TODO Auto-generated method stub
		
	}

	public void setCreatedAt(Instant now) {
		// TODO Auto-generated method stub
		
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
