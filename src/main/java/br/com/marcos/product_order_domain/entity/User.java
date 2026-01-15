package br.com.marcos.product_order_domain.entity;

import java.time.Instant;
import java.util.UUID;

// ⚠️ Idealmente isso deve ser um enum do seu domínio
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import br.com.marcos.product_order_domain.enums.Role;

@Entity
@Table(name = "user_account")
public class User {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // 🔹 Construtor padrão (obrigatório para JPA)
    public User() {
    }

    // 🔹 Construtor completo (opcional)
    public User(UUID id, String username, String passwordHash, Role role, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    /* ==========================
       JPA CALLBACK
       ========================== */

    @PrePersist
    public void prePersist() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
