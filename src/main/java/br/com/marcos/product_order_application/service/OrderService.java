package br.com.marcos.product_order_application.service;

import java.util.UUID;

import br.com.marcos.product_order_application.dto.CreateOrderRequestDTO;
import br.com.marcos.product_order_application.dto.OrderResponseDTO;

public interface OrderService {

    OrderResponseDTO createOrder(CreateOrderRequestDTO request);

    void payOrder(UUID orderId);

	void updateStock(UUID orderId);

	void updateStockOrder(UUID orderId);
}
