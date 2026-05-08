package br.com.marcos.product_order_application.service;

import br.com.marcos.product_order_application.dto.*;
import br.com.marcos.product_order_infrastructure.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class OrderReportService {

    private final OrderReportRepository repository;

    public OrderReportService(OrderReportRepository repository) {
        this.repository = repository;
    }

    public List<TopUserReportDTO> top5Users(Instant start, Instant end) {
        return repository.findTopUsers(start, end)
                .stream()
                .limit(5)
                .toList();
    }

    public List<TicketMedioReportDTO> ticketMedio(Instant start, Instant end) {
        return repository.findTicketMedioPorUsuario(start, end);
    }

    public FaturamentoMensalDTO faturamentoAtual() {
        Instant inicioMes = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        return repository.faturamentoMesAtual(inicioMes);
    }
}
