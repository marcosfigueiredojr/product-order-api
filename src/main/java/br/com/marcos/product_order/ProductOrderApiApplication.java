package br.com.marcos.product_order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages = "br.com.marcos")
@EnableJpaRepositories(basePackages = "br.com.marcos.product_order_infrastructure.repository")
@EntityScan(basePackages = "br.com.marcos.product_order_domain.entity")
public class ProductOrderApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductOrderApiApplication.class, args);
        
        // APENAS PARA TESTE: Gere um hash válido para a senha '123456'
        // Copie o resultado que aparecerá no console do Eclipse
        System.out.println("SENHA ENCRIPTADA PARA O BANCO (123456): " + new BCryptPasswordEncoder().encode("123456"));
    }
}