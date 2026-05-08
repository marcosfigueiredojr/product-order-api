package br.com.marcos.product_order_application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class TopUserReportDTO {

    private UUID userId;
    private BigDecimal totalComprado;

    // Construtor exato para a Query JPQL
    public TopUserReportDTO(UUID userId, BigDecimal totalComprado) {
        this.userId = userId;
        this.totalComprado = totalComprado;
    }

    public UUID getUserId() { return userId; }
    public BigDecimal getTotalComprado() { return totalComprado; }
}