package com.shopr.catalog.service.product;

import com.shopr.catalog.dtos.ProductDto;
import com.shopr.catalog.model.Product;
import com.shopr.catalog.request.AddProductRequest;
import com.shopr.catalog.response.ProductPageResponse;

import java.awt.print.Pageable;
import java.util.List;

public interface IProductService {
    ProductDto addProduct(AddProductRequest product);
    Product getProductById(Long id);
    ProductPageResponse getAllProducts(int pageNo, int recordCount);
    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);

    ProductPageResponse searchProducts(String search, int pageNo,int recordCount);

    void updateProduct(Long productId, AddProductRequest productRequest);

    ProductPageResponse getProductsByCategory(Long categoryId, int pageNo, int recordCount);

    List<ProductDto> getBestSellingProducts();

    void deleteProduct(Long productId);
}
