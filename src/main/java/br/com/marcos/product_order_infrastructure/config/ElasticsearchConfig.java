package br.com.marcos.product_order_infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestClient elasticsearchRestClient() {
        return RestClient.builder(new HttpHost("localhost", 9200, "http"))
            .setDefaultHeaders(new Header[]{
                // Este cabeçalho é o "pulo do gato" para resolver o erro de media-type
                new BasicHeader("Accept", "application/vnd.elasticsearch+json;compatible-with=8"),
                new BasicHeader("Content-Type", "application/vnd.elasticsearch+json;compatible-with=8")
            })
            .build();
    }

    @Bean
    public RestClientTransport restClientTransport(RestClient elasticsearchRestClient) {
        return new RestClientTransport(
            elasticsearchRestClient, 
            new JacksonJsonpMapper()
        );
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClientTransport transport) {
        return new ElasticsearchClient(transport);
    }
}