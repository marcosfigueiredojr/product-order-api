package br.com.marcos.product_order_infrastructure.config;

import br.com.marcos.product_order_application.service.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CacheWarmup {

    private final ProductService service;

    public CacheWarmup(ProductService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warm() {
        System.out.println("Iniciando Cache Warmup...");
        service.findAll(); // Isso força o preenchimento do cache logo no boot
        System.out.println("Cache preenchido com sucesso!");
    }
}