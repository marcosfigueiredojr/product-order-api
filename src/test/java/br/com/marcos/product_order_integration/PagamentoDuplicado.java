package br.com.marcos.product_order_integration;

import static org.assertj.core.api.Assertions.assertThatThrownBy; // Importe necessário para o erro da linha 51

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
public class PagamentoDuplicado {

    @Autowired
    private UserRepository userRepository;

   

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldNotAllowPayingAnAlreadyPaidOrder() {

        User user = userRepository.findByUsername("user")
            .orElseThrow(() -> new RuntimeException("Usuário 'user' não encontrado no banco de teste"));

        Product product = new Product();
        product.setName("Mouse");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(10);
        product.setCategory("Violao");
        product.setDescription("Descricao");
        product = productRepository.save(product);

        OrderItemRequestDTO item = new OrderItemRequestDTO();
        item.setProductId(product.getId());
        item.setQuantity(1);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO(user.getId(), List.of(item));

        OrderResponseDTO response = orderService.createOrder(request);

        // Act — primeiro pagamento
        orderService.payOrder(response.getId());

        // Act + Assert — segundo pagamento (Validando a exceção)
        assertThatThrownBy(() -> orderService.payOrder(response.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Pedido já foi pago");
    }
}