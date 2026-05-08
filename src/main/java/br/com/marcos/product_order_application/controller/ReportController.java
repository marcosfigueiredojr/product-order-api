package br.com.marcos.product_order_application.controller;

import br.com.marcos.product_order_application.service.OrderReportService; // Ajustado conforme a estrutura de pastas
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final OrderReportService service;

    public ReportController(OrderReportService service) {
        this.service = service;
    }

    @GetMapping("/top-users")
    public Object topUsers(
            @RequestParam Instant start,
            @RequestParam Instant end
    ) {
        return service.top5Users(start, end);
    }

    @GetMapping("/ticket-medio")
    public Object ticketMedio(
            @RequestParam Instant start,
            @RequestParam Instant end
    ) {
        return service.ticketMedio(start, end);
    }

    @GetMapping("/faturamento-mensal")
    public Object faturamentoMensal() {
        return service.faturamentoAtual();
    }
}