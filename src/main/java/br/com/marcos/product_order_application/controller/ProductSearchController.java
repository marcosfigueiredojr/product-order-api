package br.com.marcos.product_order_application.controller;

import br.com.marcos.product_order_infrastructure.elasticsearch.document.ProductDocument;
import br.com.marcos.product_order_infrastructure.elasticsearch.repository.ProductSearchRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/search")
public class ProductSearchController {

    private final ProductSearchRepository repository;

    public ProductSearchController(ProductSearchRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<ProductDocument> search(
            @RequestParam String q
    ) {
        return repository.findByNameContaining(q);
    }

    @GetMapping("/active")
    public List<ProductDocument> findActive() {
        return repository.findByActiveTrue();
    }
}
