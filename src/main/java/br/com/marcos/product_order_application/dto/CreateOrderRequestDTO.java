package br.com.marcos.product_order_application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class CreateOrderRequestDTO {

    @NotNull(message = "O ID da conta do usuário é obrigatório")
    @JsonProperty("user_account_id") 
    private UUID userAccountId;

    @JsonProperty("user_id") // ADICIONE ESTE CAMPO
    private UUID userId;
    
   	@NotEmpty(message = "A lista de itens não deve estar vazia")
    @Valid // Importante para validar os campos dentro de cada item da lista
    private List<OrderItemRequestDTO> items;

    public UUID getUserAccountId() {
    	return userAccountId; 
    	}
    public void setUserAccountId(UUID userAccountId) {
    	this.userAccountId = userAccountId;
    	}
    public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}