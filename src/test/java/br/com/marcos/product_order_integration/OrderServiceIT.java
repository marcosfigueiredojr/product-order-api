package br.com.marcos.product_order_integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.marcos.product_order_application.dto.CreateOrderRequestDTO;
import br.com.marcos.product_order_application.dto.OrderItemRequestDTO;
import br.com.marcos.product_order_application.dto.OrderResponseDTO;
import br.com.marcos.product_order_application.service.OrderService;
import br.com.marcos.product_order_domain.entity.Product;
import br.com.marcos.product_order_domain.enums.OrderStatus;
import br.com.marcos.product_order_infrastructure.repository.OrderRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;

@SpringBootTest
class OrderServiceIT extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldCreateOrderAndCalculateTotal() {

        // 1. Arrange (Preparar)
        Product product = new Product();
        product.setId(UUID.randomUUID());        product.setName("Notebook");
        product.setPrice(new BigDecimal("3500.00"));
        product.setCategory("ELETRONICOS");
        product.setStockQuantity(10);
        productRepository.save(product);

        UUID userId = UUID.randomUUID();

        // Criando o DTO que o Service espera
        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setUserAccountId(userId);

        // Criando o item do pedido
        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setProductId(product.getId());
        itemRequest.setQuantity(2); // 3500 * 2 = 7000
        
        request.setItems(List.of(itemRequest));

        // 2. Act (Executar)
        // O Service retorna um OrderResponseDTO, não a Entity diretamente
        OrderResponseDTO response = orderService.createOrder(request);

        // 3. Assert (Verificar)
        assertThat(response.getId()).isNotNull();
        
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDENTE);
        assertThat(response.getTotal()).isEqualByComparingTo("7000.00");

        // Verifica se realmente foi persistido no banco de dados
        assertThat(orderRepository.existsById(response.getId())).isTrue();
        
        var persistedOrder = orderRepository.findById(response.getId()).get();
        assertThat(persistedOrder.getItems()).hasSize(1);
    }
}