package br.com.marcos.product_order_integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

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
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
@Transactional
public class IdempotenciaEstoqueTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        // 🧼 Limpa para evitar duplicidade de chaves ou conflito entre execuções
        userRepository.deleteAll();

        // 👤 Cria e salva o usuário 'user' necessário para o teste
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");
        user.setPasswordHash("$2a$10$xyzDonutPasswordHashHereForSecurityDontChange");
        user.setRole(br.com.marcos.product_order_domain.enums.Role.ROLE_USER); // 🔄 Aqui
        userRepository.save(user);
    }

    @Test
    void shouldNotUpdateStockTwiceForSameOrder() {

        // 🎯 Agora o usuário é encontrado com sucesso!
        User user = userRepository.findByUsername("user")
              .orElseThrow(() -> new RuntimeException("Usuário 'user' não encontrado no banco de teste"));

        // Arrange
        Product product = new Product();
        product.setName("Monitor");
        product.setPrice(new BigDecimal("1000.00"));
        product.setDescription("Descricao");
        product.setCategory("Violao");
        product.setStockQuantity(5);
        product = productRepository.save(product);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(2);

        // O construtor abaixo funciona após a atualização que fizemos na DTO
        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));

        OrderResponseDTO response = orderService.createOrder(request);
        orderService.payOrder(response.getId());

        // Act — primeira atualização
        orderService.updateStock(response.getId());

        // Act — segunda atualização (simulando retry/idempotência)
        orderService.updateStock(response.getId());

        // Assert
        Product updatedProduct =
                productRepository.findById(product.getId()).orElseThrow();

        // O estoque deve ser 3 (5 original - 2 da primeira atualização). 
        // Se a segunda atualização tivesse rodado, seria 1.
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(3);

        Order order =
                orderRepository.findById(response.getId()).orElseThrow();

        assertThat(order.isStockUpdated()).isTrue();
    }
}