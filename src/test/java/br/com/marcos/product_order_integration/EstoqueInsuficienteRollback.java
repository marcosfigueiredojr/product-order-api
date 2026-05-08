package br.com.marcos.product_order_integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.test.context.support.WithMockUser;

import br.com.marcos.product_order_application.dto.CreateOrderRequestDTO;
import br.com.marcos.product_order_application.dto.OrderItemRequestDTO;
import br.com.marcos.product_order_application.dto.OrderResponseDTO;
import br.com.marcos.product_order_application.service.OrderService;
import br.com.marcos.product_order_domain.entity.Order;
import br.com.marcos.product_order_domain.entity.Product;
import br.com.marcos.product_order_domain.entity.User;
import br.com.marcos.product_order_infrastructure.repository.OrderRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import br.com.marcos.product_order_infrastructure.repository.UserRepository;


@SpringBootTest(classes = br.com.marcos.product_order.ProductOrderApiApplication.class)
@WithMockUser(username = "user", roles = {"USER"})
@Transactional
public class EstoqueInsuficienteRollback {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRollbackWhenStockIsInsufficient() {

        // Arrange - Busca o usuário existente no banco
        User user = userRepository.findByUsername("user")
                .orElseThrow(() -> new RuntimeException("Usuário 'user' não encontrado no banco de teste"));

        Product product = new Product();
        product.setName("Teclado");
        product.setPrice(new BigDecimal("300.00"));
        product.setStockQuantity(1);
        product.setDescription("Descricao");
        product.setCategory("Violao");
        product = productRepository.save(product);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(2);

        // Usa o ID do usuário que já existe na tabela
        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));

        OrderResponseDTO response = orderService.createOrder(request);
        orderService.payOrder(response.getId());

        // Act + Assert
        assertThatThrownBy(() -> orderService.updateStock(response.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Estoque insuficiente");

        Product reloadedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(reloadedProduct.getStockQuantity()).isEqualTo(1);

        Order order = orderRepository.findById(response.getId()).orElseThrow();
        assertThat(order.isStockUpdated()).isFalse();
    }
}