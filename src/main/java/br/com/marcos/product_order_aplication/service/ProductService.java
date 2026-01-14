package br.com.marcos.product_order_aplication.service;

import java.util.List;
import java.util.UUID;

import br.com.marcos.product_order_api.dto.ProductRequestDTO;
import br.com.marcos.product_order_api.dto.ProductResponseDTO;

public interface ProductService {

    ProductResponseDTO create(ProductRequestDTO request);

    ProductResponseDTO update(UUID id, ProductRequestDTO request);

    ProductResponseDTO findById(UUID id);

    List<ProductResponseDTO> findAll();

    void delete(UUID id);
}
