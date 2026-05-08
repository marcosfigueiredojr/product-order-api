package br.com.marcos.product_order_infrastructure.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer)
                        );

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(
                "product-by-id",
                defaultConfig.entryTtl(Duration.ofMinutes(30))
        );

        configs.put(
                "product-list",
                defaultConfig.entryTtl(Duration.ofMinutes(5))
        );

        configs.put(
                "product-category",
                defaultConfig.entryTtl(Duration.ofMinutes(15))
        );

        configs.put(
                "reports",
                defaultConfig.entryTtl(Duration.ofMinutes(60))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configs)
                .transactionAware()
                .build();
    }
}