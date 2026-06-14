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
import br.com.marcos.product_order_domain.entity.UserAccount;
import br.com.marcos.product_order_domain.enums.OrderStatus;
import br.com.marcos.product_order_infrastructure.repository.OrderRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import br.com.marcos.product_order_infrastructure.repository.UserAccountRepository;

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
    private UserAccountRepository userAccountRepository;

    private UUID userAccountId;

    @BeforeEach
    void setUp() {
        // 🧼 Garante que o repositório está limpo antes de reinserir para evitar conflito de chaves
        userAccountRepository.deleteAll();

        // 👤 Gera o ID dinâmico que será usado na request do teste
        this.userAccountId = UUID.randomUUID();

        // 💾 Salva o usuário no banco de dados de teste para passar na validação do OrderServiceImpl
        UserAccount user = new UserAccount();
        user.setId(this.userAccountId);
        user.setUsername("user");
        user.setPasswordHash("$2a$10$xyzDonutPasswordHashHereForSecurityDontChange");
        user.setRole("ROLE_USER");

        userAccountRepository.save(user);
    }

    @Test
    void shouldCreatePayAndUpdateStockSuccessfully() {

        // =========================
        // Arrange — cria produto
        // =========================
        Product product = new Product();
        product.setName("Notebook");
        product.setPrice(new BigDecimal("2500.00"));
        product.setStockQuantity(10);
        product.setDescription("Descricao");
        product.setCategory("Violao");

        product = productRepository.save(product);

        // =========================
        // Arrange — item do pedido
        // =========================
        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(2);

        List<OrderItemRequestDTO> items = new ArrayList<>();
        items.add(item);

        // =========================
        // Arrange — request
        // =========================
        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        // 🎯 Agora utilizamos o ID do usuário que foi salvo previamente no banco pelo @BeforeEach
        request.setUserAccountId(this.userAccountId);
        request.setItems(items);

        // =========================
        // Act — CREATE
        // =========================
        OrderResponseDTO response = orderService.createOrder(request);

        // =========================
        // Assert — CREATE
        // =========================
        assertThat(response.getId()).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDENTE);
        assertThat(response.getTotal()).isEqualByComparingTo("5000.00");

        // =========================
        // Act — PAY
        // =========================
        orderService.payOrder(response.getId());

        Order paidOrder =
                orderRepository.findById(response.getId()).orElseThrow();

        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.PAGO);

        // =========================
        // Act — STOCK UPDATE
        // =========================
        orderService.updateStock(paidOrder.getId());

        Product updatedProduct =
                productRepository.findById(product.getId()).orElseThrow();

        assertThat(updatedProduct.getStockQuantity()).isEqualTo(8);

        Order finalOrder =
                orderRepository.findById(paidOrder.getId()).orElseThrow();

        assertThat(finalOrder.isStockUpdated()).isTrue();
    }
}