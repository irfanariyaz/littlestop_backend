package com.shopr.catalog.controller;

import com.shopr.catalog.dtos.ProductDto;
import com.shopr.catalog.exceptions.AlreadyExistException;
import com.shopr.catalog.exceptions.ResourceNotFoundException;
import com.shopr.catalog.model.Product;
import com.shopr.catalog.request.AddProductRequest;
import com.shopr.catalog.response.ApiResponse;
import com.shopr.catalog.response.ProductPageResponse;
import com.shopr.catalog.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/")
@Slf4j
public class ProductController {
    private final IProductService productService;

    //get all products
    @GetMapping("/all/{pageNo}/{recordCount}")
    public ResponseEntity<ApiResponse> getAllProducts(@PathVariable int pageNo, @PathVariable int recordCount) {
        log.info("request reached to get all products"+pageNo+":"+recordCount);
        try {
          ProductPageResponse pageResponse = productService.getAllProducts(pageNo,recordCount);
            return ResponseEntity.ok(new ApiResponse("Success", pageResponse));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
        //  List<ProductDto> convertedProducts = productService.getConvertedProducts(products);

    }
    //get product by name or brand
    @GetMapping("/search/{search}/{pageNo}/{recordCount}")
    public  ResponseEntity<ApiResponse> searchProducts(@PathVariable String search,
                                                       @PathVariable int pageNo,
                                                       @PathVariable int recordCount){
        log.info("request reached to search products"+pageNo+ "+"+recordCount+"STRINg"+search);
        try {
            ProductPageResponse pageResponse  = productService.searchProducts(search,pageNo,recordCount);
            return ResponseEntity.ok(new ApiResponse("Success", pageResponse));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
    //get products by category
    @GetMapping("/category/{categoryId}/{pageNo}/{recordCount}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                             @PathVariable int pageNo,
                                                             @PathVariable int recordCount) {
        log.info("request reached to get products by category");
        log.info("request reached to get all products by category"+pageNo+":"+recordCount);
        try {
            ProductPageResponse pageResponse = productService.getProductsByCategory(categoryId, pageNo, recordCount);
            return ResponseEntity.ok(new ApiResponse("Success", pageResponse));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/{productId}")
    public  ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId){
        log.info("request reached to get product by id");
        try {
            Product product = productService.getProductById(productId);
            ProductDto  productDto = productService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("Success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
   // @PreAuthorize("hasRole('ROLE_ADMIN') ")
    //add a product
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(
            @RequestPart AddProductRequest productRequest,
            @RequestPart("images") List<MultipartFile> files
    ) {
        System.out.println("product reached backend");
        try {
           productRequest.setFiles(files);
            ProductDto newProduct = productService.addProduct(productRequest);
            return ResponseEntity.ok(new ApiResponse("Add product Success!", newProduct));
        } catch (AlreadyExistException e) {
            return ResponseEntity.status(CONFLICT)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Long productId,
                                                     @RequestPart AddProductRequest productRequest,
                                                     @Nullable @RequestPart("images") List<MultipartFile> files) {
        log.info("request reached to update product");
        try {
            if(files!=null) {
                productRequest.setFiles(files);
            }
            productService.updateProduct(productId, productRequest);
            return ResponseEntity.ok(new ApiResponse("Update product Success!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
    //get the products which are best selling
    @GetMapping("/best-selling")
    public ResponseEntity<ApiResponse> getBestSellingProducts(){
        log.info("request reached to get best selling products");
        try {
            List<ProductDto> products = productService.getBestSellingProducts();
            return ResponseEntity.ok(new ApiResponse("Success", products));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
    //delete a product
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId){
        log.info("request reached to delete product");
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.ok(new ApiResponse("Deleted the product Successfully!", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }




}
