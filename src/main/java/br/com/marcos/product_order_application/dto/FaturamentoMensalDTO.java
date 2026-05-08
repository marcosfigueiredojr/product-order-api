package br.com.marcos.product_order_application.dto;

import java.math.BigDecimal;

public class FaturamentoMensalDTO {

    private BigDecimal total;

    public FaturamentoMensalDTO(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }
}