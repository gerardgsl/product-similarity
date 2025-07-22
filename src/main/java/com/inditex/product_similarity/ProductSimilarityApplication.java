package com.inditex.product_similarity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.inditex.product_similarity.client")
@EnableCaching
public class ProductSimilarityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductSimilarityApplication.class, args);
	}

}
