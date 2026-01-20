package br.com.marcos.product_order_application.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.marcos.product_order_application.dto.ProductRequestDTO;
import br.com.marcos.product_order_application.dto.ProductResponseDTO;
import br.com.marcos.product_order_application.service.ProductService;
import br.com.marcos.product_order_domain.entity.Product;
import br.com.marcos.product_order_domain.exceptions.ResourceNotFoundException;
import br.com.marcos.product_order_infrastructure.elasticsearch.mapper.ProductSearchMapper;
import br.com.marcos.product_order_infrastructure.elasticsearch.repository.ProductSearchRepository;
import br.com.marcos.product_order_infrastructure.repository.ProductRepository;


@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductSearchRepository searchRepository;


    public ProductServiceImpl(ProductRepository repository,
    		ProductSearchRepository searchRepository) {
        this.repository = repository;
        this.searchRepository = searchRepository;
    }

    @Override
    public ProductResponseDTO create(ProductRequestDTO request) {
        Product product = new Product();
        updateEntityFromDto(request, product);
        Product saved = repository.save(product);
        searchRepository.save(
                ProductSearchMapper.toDocument(saved));
        
        return toResponse(saved);
    }

    @Override
    public ProductResponseDTO update(UUID id, ProductRequestDTO request) {
        Product product = repository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        updateEntityFromDto(request, product);
        searchRepository.save(
        ProductSearchMapper.toDocument(product)
        );
        return toResponse(repository.save(product));
    }

    private void updateEntityFromDto(ProductRequestDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStockQuantity(dto.getStock());  
        entity.setCategory(dto.getCategory());
        entity.setActive(dto.getActive());
    }

    private ProductResponseDTO toResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCategory()
        );
    }
 
    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO findById(UUID id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        repository.deleteById(id);
        searchRepository.deleteById(id);
    }

    /* =========================
       Mapper manual
       ========================= */

  
 }
