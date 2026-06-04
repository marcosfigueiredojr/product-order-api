package br.com.marcos.product_order_infrastructure.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheLogAspect {

    private static final Logger log = LoggerFactory.getLogger(CacheLogAspect.class);

    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object logCache(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = jp.proceed();
        long end = System.currentTimeMillis();

        log.info(
                "Cache method={} time={}ms",
                jp.getSignature().getName(),
                end - start
        );

        return result;
    }
}
    

