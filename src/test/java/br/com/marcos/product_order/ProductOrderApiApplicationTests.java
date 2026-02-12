package br.com.marcos.product_order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @Testcontainers // Comentado para não tentar criar novos containers automaticamente
@SpringBootTest
class ProductOrderApiApplicationTests {

    /* COMENTADO: Bloco de configuração do Testcontainers que estava gerando erro 400
    static {
        System.setProperty("DOCKER_HOST", "tcp://127.0.0.1:2375");
        System.setProperty("TESTCONTAINERS_RYUK_DISABLED", "true");
    }
    */

    /* COMENTADO: Não vamos mais subir o Elasticsearch via código, 
       usaremos o que já está rodando no Docker Desktop
    // @Container
    // static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
    //         "docker.elastic.co/elasticsearch/elasticsearch:8.10.2")
    //         .withEnv("discovery.type", "single-node")
    //         .withEnv("xpack.security.enabled", "false");
    */

    /*
       COMENTADO: O Spring agora vai ler as URLs do seu application.yml padrão
    // @DynamicPropertySource
    // static void setProperties(DynamicPropertyRegistry registry) {
    //     registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
    // }
    */

    @Test
    void contextLoads() {
        // Agora o teste apenas verifica se o Spring consegue subir e conectar 
        // no MySQL (porta 3307) e no Elastic (porta 9200) que já estão ativos.
        System.out.println("✅ Teste de contexto iniciado utilizando containers locais.");
    }
}