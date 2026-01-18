package br.com.marcos.product_order_application.service;

import java.util.List;
import java.util.UUID;

import br.com.marcos.product_order_application.dto.ProductRequestDTO;
import br.com.marcos.product_order_application.dto.ProductResponseDTO;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProductService {
	
	@PreAuthorize("hasRole('ADMIN')")
    ProductResponseDTO create(ProductRequestDTO request);

	@PreAuthorize("hasRole('ADMIN')")
    ProductResponseDTO update(UUID id, ProductRequestDTO request);

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
    ProductResponseDTO findById(UUID id);

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
    List<ProductResponseDTO> findAll();

    @PreAuthorize("hasRole('ADMIN')")
    void delete(UUID id);
}
