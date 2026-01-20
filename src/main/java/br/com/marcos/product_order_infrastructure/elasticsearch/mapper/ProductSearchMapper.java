package br.com.marcos.product_order_infrastructure.elasticsearch.mapper;

import br.com.marcos.product_order_domain.entity.Product;
import br.com.marcos.product_order_infrastructure.elasticsearch.document.ProductDocument;

public class ProductSearchMapper {

    public static ProductDocument toDocument(Product product) {

        ProductDocument doc = new ProductDocument();
        doc.setId(product.getId());
        doc.setName(product.getName());
        doc.setDescription(product.getDescription());
        doc.setPrice(product.getPrice());
        doc.setStock(product.getStockQuantity());
        doc.setActive(product.getActive());

        return doc;
    }
}
