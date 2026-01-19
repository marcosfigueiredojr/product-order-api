package br.com.marcos.product_order_application.controller;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.marcos.product_order_application.dto.CreateOrderRequestDTO;
import br.com.marcos.product_order_application.dto.OrderResponseDTO;
import br.com.marcos.product_order_application.service.OrderService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateOrderRequestDTO request) {
        try {
            OrderResponseDTO response = service.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Imprime o erro detalhado no console do IDE/Terminal
            System.err.println("Erro ao criar pedido: " + e.getMessage());
            e.printStackTrace(); 
            
            // Retorna o erro para o cliente (Postman)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable UUID id) {
        try {
            service.payOrder(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Imprime o erro detalhado no console
            System.err.println("Erro ao processar pagamento do pedido " + id + ": " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body("Erro ao pagar: " + e.getMessage());
        }
    }
}