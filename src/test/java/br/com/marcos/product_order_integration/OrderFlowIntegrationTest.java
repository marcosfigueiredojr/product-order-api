package br.com.marcos.product_order_integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import br.com.marcos.product_order_domain.enums.OrderStatus;
import br.com.marcos.product_order_domain.enums.Role;
import br.com.marcos.product_order_infrastructure.repository.OrderRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import br.com.marcos.product_order_infrastructure.repository.UserRepository;

@SpringBootTest(classes = br.com.marcos.product_order.ProductOrderApiApplication.class)
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
@Transactional
class OrderFlowIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private UUID userAccountId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        this.userAccountId = UUID.randomUUID();

        User user = new User();
        user.setId(this.userAccountId);
        user.setUsername("user");
        user.setPasswordHash("$2a$10$xyzDonutPasswordHashHereForSecurityDontChange");
        user.setRole(Role.ROLE_USER); // 🔄 Corrigido para usar Enum

        userRepository.save(user);
    }

    @Test
    void shouldCreatePayAndUpdateStockSuccessfully() {
        Product product = new Product();
        product.setName("Notebook");
        product.setPrice(new BigDecimal("2500.00"));
        product.setStockQuantity(10);
        product.setDescription("Descricao");
        product.setCategory("Violao");

        product = productRepository.save(product);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(2);

        List<OrderItemRequestDTO> items = new ArrayList<>();
        items.add(item);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setUserAccountId(this.userAccountId);
        request.setItems(items);

        OrderResponseDTO response = orderService.createOrder(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDENTE);
        assertThat(response.getTotal()).isEqualByComparingTo("5000.00");

        orderService.payOrder(response.getId());

        Order paidOrder = orderRepository.findById(response.getId()).orElseThrow();
        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.PAGO);

        orderService.updateStock(paidOrder.getId());

        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(8);

        Order finalOrder = orderRepository.findById(paidOrder.getId()).orElseThrow();
        assertThat(finalOrder.isStockUpdated()).isTrue();
    }
}