package com.shopr.catalog.response;

import com.shopr.catalog.dtos.ProductDto;
import lombok.Data;

import java.util.List;
@Data
public class ProductPageResponse {
    private List<ProductDto> products;
    private boolean isLast;

    public ProductPageResponse(List<ProductDto> products, boolean isLast) {
        this.products = products;
        this.isLast = isLast;
    }
}
