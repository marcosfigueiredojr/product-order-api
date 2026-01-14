package br.com.marcos.product_order_infrastructure.repository;

import br.com.marcos.product_order_domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
