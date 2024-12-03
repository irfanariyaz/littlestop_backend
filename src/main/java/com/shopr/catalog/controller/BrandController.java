package com.shopr.catalog.controller;

import com.shopr.catalog.model.Brand;
import com.shopr.catalog.model.Category;
import com.shopr.catalog.response.ApiResponse;
import com.shopr.catalog.service.brand.IBrandService;
import com.shopr.catalog.service.category.ICategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("${api.prefix}/brands")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000/")
@Slf4j
public class BrandController {
    private  final IBrandService brandService;

    @GetMapping("/all")
    private ResponseEntity<ApiResponse> getAllBrands(){
        try {
            List<Brand> brands = brandService.getAllBrands();
            System.out.println("length of brand"+brands.size());
            return ResponseEntity.ok(new ApiResponse("Found", brands));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Error", INTERNAL_SERVER_ERROR));
        }
    }
}
