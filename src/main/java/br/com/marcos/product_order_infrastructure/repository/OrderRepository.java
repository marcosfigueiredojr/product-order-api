package br.com.marcos.product_order_infrastructure.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.marcos.product_order_domain.entity.Order;
import br.com.marcos.product_order_domain.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserId(UUID userId);

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.status = :status
          AND o.createdAt BETWEEN :start AND :end
    """)
    List<Order> findByStatusAndPeriod(
            @Param("status") OrderStatus status,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
