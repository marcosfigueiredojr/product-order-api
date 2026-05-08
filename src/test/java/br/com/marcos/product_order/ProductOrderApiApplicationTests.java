package br.com.marcos.product_order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
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
import br.com.marcos.product_order_domain.entity.OrderItem;
import br.com.marcos.product_order_domain.entity.Product;
import br.com.marcos.product_order_domain.entity.User;
import br.com.marcos.product_order_domain.enums.OrderStatus;
import br.com.marcos.product_order_domain.exceptions.BusinessException;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import br.com.marcos.product_order_infrastructure.repository.UserRepository;

@SpringBootTest(classes = br.com.marcos.product_order.ProductOrderApiApplication.class)
@WithMockUser(username = "user", roles = {"USER"})
@Transactional
class ProductOrderApiApplicationTests {

    static {
        System.setProperty("DOCKER_HOST", "tcp://127.0.0.1:2375");
        System.setProperty("TESTCONTAINERS_RYUK_DISABLED", "true");
    }

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        System.out.println("✅ Teste de contexto iniciado utilizando containers locais.");
    }

    // --- MÉTODOS AUXILIARES ---

    private Product createValidProduct(String name, int quantity) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(quantity);
        product.setCategory("Eletrônicos");
        product.setDescription("Descrição padrão"); 
        return productRepository.save(product);
    }   

    // --- TESTES DE INTEGRAÇÃO (SERVIÇO) ---

    @Test
    void shouldRollbackWhenStockIsInsufficient() {
        User user = userRepository.findByUsername("user").orElseThrow();
        Product product = createValidProduct("Guitarra", 1);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(2); 

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));

        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");

        Product reloaded = productRepository.findById(product.getId()).orElseThrow();
        assertThat(reloaded.getStockQuantity()).isEqualTo(1);
    }

    @Test
    void shouldNotUpdateStockTwiceForSameOrder() {
        User user = userRepository.findByUsername("user").orElseThrow();
        Product product = createValidProduct("Monitor", 5);
        
        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(2);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));
        OrderResponseDTO response = orderService.createOrder(request);
        orderService.payOrder(response.getId());

        orderService.updateStock(response.getId());
        orderService.updateStock(response.getId());

        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(3);
    }

    @Test
    void shouldNotAllowPayingAnAlreadyPaidOrder() {
        User user = userRepository.findByUsername("user").orElseThrow();
        Product product = createValidProduct("Mouse", 10);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(1);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));
        OrderResponseDTO response = orderService.createOrder(request);

        orderService.payOrder(response.getId());

        assertThatThrownBy(() -> orderService.payOrder(response.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Only pending orders can be paid");
    }

    @Test
    void shouldNotUpdateStockIfOrderIsNotPaid() {
        User user = userRepository.findByUsername("user").orElseThrow();
        Product product = createValidProduct("Teclado", 10);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(1);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));
        OrderResponseDTO response = orderService.createOrder(request);

        assertThatThrownBy(() -> orderService.updateStock(response.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pedido não está pago");
    }

    // --- TESTES DA ENTIDADE ORDER (COM UUID) ---

    @Test
    @DisplayName("Entidade: Deve criar pedido com status pendente")
    void shouldCreateOrderWithPendingStatus() {
        UUID userId = UUID.randomUUID();
        UUID userAccountId = UUID.randomUUID();

        Order order = new Order(userId, userAccountId);

        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getUserAccountId()).isEqualTo(userAccountId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDENTE);
        assertThat(order.getTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(order.getItems()).isEmpty();
        assertThat(order.isStockUpdated()).isFalse();
    }

    @Test
    @DisplayName("Entidade: Deve adicionar item e recalcular total")
    void shouldAddItemAndRecalculateTotal() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID());

        OrderItem item = new OrderItem();
        item.setQuantity(2);
        item.setUnitPriceSnapshot(new BigDecimal("10.00"));
        item.setSubtotal(new BigDecimal("20.00"));

        order.addItem(item);

        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getTotal()).isEqualByComparingTo("20.00");
    }

    @Test
    @DisplayName("Entidade: Deve marcar estoque como atualizado")
    void shouldMarkStockAsUpdated() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID());

        order.markStockUpdated();

        assertThat(order.isStockUpdated()).isTrue();
    }

    @Test
    @DisplayName("Entidade: Não deve permitir atualizar estoque duas vezes")
    void shouldNotAllowStockUpdateTwice() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID());
        order.markStockUpdated();
           assertThat(order.isStockUpdated()).isTrue();

        assertThatThrownBy(order::markStockUpdated)
           .isInstanceOf(BusinessException.class)
           .hasMessageContaining("Stock already updated");
    }

    @Test
    @DisplayName("Entidade: Deve permitir alterar status para pago")
    void shouldChangeStatusToPaid() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID());

        order.setStatus(OrderStatus.PAGO);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAGO);
    }
}