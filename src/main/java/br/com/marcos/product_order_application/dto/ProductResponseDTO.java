package br.com.marcos.product_order_application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;

    public ProductResponseDTO() {}

    public ProductResponseDTO(UUID id, String name, String description, BigDecimal price, Integer stock, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getStock() { return stock; }
    public String getCategory() { return category; }
}