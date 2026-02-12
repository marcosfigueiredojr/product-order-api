package br.com.marcos.product_order_infrastructure.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.UUID;

@Document(indexName = "ecommerce")
public class ProductDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Boolean)
    private Boolean active;

    public ProductDocument() {}

	public void setId(UUID id2) {
		// TODO Auto-generated method stub
		
	}

	public void setName(String name2) {
		// TODO Auto-generated method stub
		
	}

	public void setDescription(String description2) {
		// TODO Auto-generated method stub
		
	}

	public void setPrice(BigDecimal price2) {
		// TODO Auto-generated method stub
		
	}

	public void setStock(Integer stockQuantity) {
		// TODO Auto-generated method stub
		
	}

	public void setActive(Object active2) {
		// TODO Auto-generated method stub
		
	}

    // getters e setters
}
