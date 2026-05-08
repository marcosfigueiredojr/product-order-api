package br.com.marcos.product_order_integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.marcos.product_order_domain.entity.Order;
import br.com.marcos.product_order_domain.entity.OrderItem;
import br.com.marcos.product_order_domain.enums.OrderStatus;
import br.com.marcos.product_order_domain.exceptions.BusinessException;

class OrderTest {

    @Test
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
    void shouldMarkStockAsUpdated() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID());

        order.markStockUpdated();

        assertThat(order.isStockUpdated()).isTrue();
    }

    @Test
    void shouldNotAllowStockUpdateTwice() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID());
        order.markStockUpdated();

        assertThatThrownBy(order::markStockUpdated)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Stock already updated");
    }

    @Test
    void shouldChangeStatusToPaid() {
        Order order = new Order(UUID.randomUUID(), UUID.randomUUID());

        order.setStatus(OrderStatus.PAGO);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAGO);
    }
}
