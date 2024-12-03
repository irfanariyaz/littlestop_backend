package com.shopr.catalog.repo;

import com.shopr.catalog.model.Brand;
import com.shopr.catalog.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Boolean existsByNameAndBrand(String name, Brand brand);

    Page<Product> findByNameContainingIgnoreCaseOrBrandNameContainingIgnoreCase(String search, String search1, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    List<Product> findByIsBestSellingTrue();
}
