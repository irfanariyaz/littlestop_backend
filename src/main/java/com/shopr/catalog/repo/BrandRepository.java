package com.shopr.catalog.repo;

import com.shopr.catalog.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends  JpaRepository<Brand, Long>{


    Boolean existsByName(String brand);

    Optional<Brand> findByName(String brand);
}
