package br.com.marcos.product_order_infrastructure.elasticsearch.repository;

import br.com.marcos.product_order_infrastructure.elasticsearch.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, UUID> {

    List<ProductDocument> findByNameContaining(String name);

    List<ProductDocument> findByActiveTrue();
}
