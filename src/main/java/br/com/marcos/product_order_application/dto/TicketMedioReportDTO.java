package br.com.marcos.product_order_application.dto;

import java.util.UUID;

public class TicketMedioReportDTO {

    private UUID userId;
    private Double ticketMedio;

    // Construtor exato para a Query JPQL
    public TicketMedioReportDTO(UUID userId, Double ticketMedio) {
        this.userId = userId;
        this.ticketMedio = ticketMedio;
    }

    public UUID getUserId() { return userId; }
    public Double getTicketMedio() { return ticketMedio; }
}