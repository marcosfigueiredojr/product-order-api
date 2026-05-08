package br.com.marcos.product_order_integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import br.com.marcos.product_order_application.dto.CreateOrderRequestDTO;
import br.com.marcos.product_order_application.dto.OrderItemRequestDTO;
import br.com.marcos.product_order_application.dto.OrderResponseDTO;
import br.com.marcos.product_order_application.service.OrderService;
import br.com.marcos.product_order_domain.entity.Product;
import br.com.marcos.product_order_domain.entity.User;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;
import br.com.marcos.product_order_infrastructure.repository.UserRepository;

@SpringBootTest(classes = br.com.marcos.product_order.ProductOrderApiApplication.class)
@WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
@Transactional
public class VerificacaoPagamentoEstoqueTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void IllegalStateException() {
        
        User user = userRepository.findByUsername("user")
            .orElseThrow(() -> new RuntimeException("Usuário 'user' não encontrado no banco de teste"));

        // Arrange
        Product product = new Product();
        product.setName("Headset");
        product.setPrice(new BigDecimal("400.00"));
        product.setStockQuantity(5);
        product.setDescription("Descricao");
        product.setCategory("Violao");
        product = productRepository.save(product);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(1);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));

        // Cria o pedido, mas NÃO chama o payOrder()
        OrderResponseDTO response = orderService.createOrder(request);

        // Act + Assert
        // Verifica se o sistema lança exceção ao tentar atualizar estoque de pedido não pago
        assertThatThrownBy(() -> orderService.updateStock(response.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pedido não está pago");

        // Verifica se o estoque permanece intacto (deve continuar sendo 5)
        Product reloadedProduct =
                productRepository.findById(product.getId()).orElseThrow();

        assertThat(reloadedProduct.getStockQuantity()).isEqualTo(5);
    }
}