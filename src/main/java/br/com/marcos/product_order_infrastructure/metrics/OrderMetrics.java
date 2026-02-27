package br.com.marcos.product_order_infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {

    private final Counter orderCreatedCounter;

    public OrderMetrics(MeterRegistry registry) {
        this.orderCreatedCounter =
                registry.counter("orders.created");
    }

    public void incrementOrderCreated() {
        orderCreatedCounter.increment();
    }
}
