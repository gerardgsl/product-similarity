package com.inditex.product_similarity.mapper;

import com.inditex.product_similarity.model.Product;
import com.inditex.dto.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "price", target = "price", qualifiedByName = "doubleToBigDecimal")
    ProductDetail toDto(Product product);

    @Named("doubleToBigDecimal")
    default BigDecimal mapDoubleToBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }

    default List<ProductDetail> toDtoList(List<Product> products) {
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    default Set<ProductDetail> toDtoSet(List<Product> products) {
        return products.stream().map(this::toDto).collect(Collectors.toSet());
    }
}