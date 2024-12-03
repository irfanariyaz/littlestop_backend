package com.shopr.catalog.dtos;


import com.shopr.catalog.model.Category;
import com.shopr.catalog.model.ProductImage;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDto {
    private  Long id;
    private  String name;
    private String brand;
    private  String description;
    private BigDecimal price;
    private  int inventory;    //quantity.to tract number of product
    private String category;
    private  Boolean isBestSelling;
    private List<ImageDto> images;
}