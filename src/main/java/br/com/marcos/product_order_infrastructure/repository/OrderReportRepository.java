package br.com.marcos.product_order_infrastructure.repository;

import br.com.marcos.product_order_domain.entity.Order;
import br.com.marcos.product_order_application.dto.FaturamentoMensalDTO;
import br.com.marcos.product_order_application.dto.TicketMedioReportDTO;
import br.com.marcos.product_order_application.dto.TopUserReportDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderReportRepository extends JpaRepository<Order, UUID> {

    @Query("""
        SELECT new br.com.marcos.product_order_application.dto.TopUserReportDTO(
            o.userId,
            SUM(o.total)
        )
        FROM OrderEntity o
        WHERE o.status = br.com.marcos.product_order_domain.enums.OrderStatus.PAGO
          AND o.createdAt BETWEEN :start AND :end
        GROUP BY o.userId
        ORDER BY SUM(o.total) DESC
    """)
    List<TopUserReportDTO> findTopUsers(@Param("start") Instant start, @Param("end") Instant end);

    @Query("""
        SELECT new br.com.marcos.product_order_application.dto.TicketMedioReportDTO(
            o.userId,
            AVG(o.total)
        )
        FROM OrderEntity o
        WHERE o.status = br.com.marcos.product_order_domain.enums.OrderStatus.PAGO
          AND o.createdAt BETWEEN :start AND :end
        GROUP BY o.userId
    """)
    List<TicketMedioReportDTO> findTicketMedioPorUsuario(@Param("start") Instant start, @Param("end") Instant end);

    @Query("""
        SELECT new br.com.marcos.product_order_application.dto.FaturamentoMensalDTO(
            COALESCE(SUM(o.total), 0)
        )
        FROM OrderEntity o
        WHERE o.status = br.com.marcos.product_order_domain.enums.OrderStatus.PAGO
          AND o.createdAt >= :inicioMes
    """)
    FaturamentoMensalDTO faturamentoMesAtual(@Param("inicioMes") Instant inicioMes);
}