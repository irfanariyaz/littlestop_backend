package com.shopr.catalog.request;

import com.shopr.catalog.model.Category;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AddProductRequest {
    private  Long id;
    private  String name;
    private String brand;
    private  String description;
    private BigDecimal price;
    private  int inventory;    //quantity.to tract number of product
    private String category;
    private List<MultipartFile> files;
    private  int thumbnailIndex;
    private  boolean isBestSelling;
}
