package com.inditex.product_similarity.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

import org.springframework.stereotype.Service;

import com.inditex.product_similarity.client.ProductClient;
import com.inditex.product_similarity.model.Product;

import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {
    
    private final ProductClient productClient;

    public ProductServiceImpl(ProductClient client) {
        this.productClient = client;
    }

    public List<Product> getSimilarProducts(String productId) {
        List<String> ids = productClient.getSimilarProductIds(productId);
        return ids.parallelStream()               // llamadas concurrentes
                  .map(this::getProductSafe)      // devuelve null si falla
                  .filter(p -> p != null)         // elimino fallos
                  .collect(Collectors.toList());
    }

    @Retry(name = "product-api", fallbackMethod = "fallbackProduct")
    @CircuitBreaker(name = "product-api", fallbackMethod = "fallbackProduct")
    @TimeLimiter(name = "product-api")
    public CompletableFuture<Product> getProductAsync(String id) {
        // Ejecutamos la llamada Feign en un hilo aparte
        return CompletableFuture.supplyAsync(() -> productClient.getProductById(id));
    }


    /** Adaptador síncrono: espera el resultado de getProductAsync o null si falla. */
    private Product getProductSafe(String id) {
        try {
            return getProductAsync(id).join();  // join() propaga excepciones como unchecked
        } catch (Exception ex) {
            return null;                        // seguridad extra (no debería llegar)
        }
    }

     /* ----------  Fallbacks  ---------- */
    
     @SuppressWarnings("unused")
    private CompletableFuture<Product> fallbackProduct(String id, Throwable t) {
        // Aquí podrías loggear el error o enviar métricas
        return CompletableFuture.completedFuture(null);
    }
}
