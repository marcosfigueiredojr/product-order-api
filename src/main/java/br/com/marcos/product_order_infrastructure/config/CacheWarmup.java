package br.com.marcos.product_order_infrastructure.config;

import br.com.marcos.product_order_application.service.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CacheWarmup {

    private final ProductService service;

    public CacheWarmup(ProductService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warm() {
        System.out.println("Iniciando Cache Warmup...");
        try {
            // 🔐 Injeta um usuário interno para passar pelas anotações de @PreAuthorize nos testes
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "system", 
                null, 
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")) // Caso sua role mude, ajuste aqui
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            service.findAll(); // Isso força o preenchimento do cache logo no boot
            System.out.println("Cache preenchido com sucesso!");
            
        } finally {
            // 🧹 Limpa o contexto para não interferir nas próximas requisições
            SecurityContextHolder.clearContext();
        }
    }
}