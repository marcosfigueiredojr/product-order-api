package br.com.marcos.product_order_domain.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.marcos.product_order_domain.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_orders")
public class Order {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;
    
    @Column(name = "user_account_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userAccountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal total;

    // flag idempotencia
    @Column(name = "stock_updated", nullable = false)
    private boolean stockUpdated = false;


    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // 🔹 Construtor padrão (obrigatório para JPA)
    public Order() {
    }

    // 🔹 Construtor completo (opcional, útil em testes)
    public Order(
            UUID id,
            UUID userId,
            OrderStatus status,
            BigDecimal total,
            List<OrderItem> items,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.total = total;
        this.items = items != null ? items : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /* ==========================
       JPA CALLBACKS
       ========================== */

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    /* ==========================
       DOMAIN METHODS
       ========================== */

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
    }

    /* ==========================
       GETTERS AND SETTERS
       ========================== */

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserAccountId() {
    	return userAccountId; 
    	}
    public void setUserAccountId(UUID userAccountId) { 
    	this.userAccountId = userAccountId; 
    }
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

	public void setUser(User user) {
		// TODO Auto-generated method stub
		
	}

	public void setCreatedAt(Instant now) {
		// TODO Auto-generated method stub
		
	}
}
