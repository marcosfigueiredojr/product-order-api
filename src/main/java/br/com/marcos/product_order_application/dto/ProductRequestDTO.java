package br.com.marcos.product_order_application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductRequestDTO {

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must have at most 150 characters")
    private String name;
    
    @Size(max = 500, message = "Description must have at most 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;
    
    @NotNull(message = "Stock is required")
    @Positive(message = "Stock must be greater than zero")
    private Integer stock;

    @NotBlank(message = "Category is required")
    private String category;

    public ProductRequestDTO() {
    }

    // Getters e Setters (Incluindo o novo campo)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

	public Object getActive() {
		// TODO Auto-generated method stub
		return null;
	}
}