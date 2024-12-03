package com.shopr.catalog.repo;

import com.shopr.catalog.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findByProductId(Long id);

    Optional<ProductImage> findByProductIdAndIsThumbnail(Long id, boolean b);

    void deleteByProductId(Long productId);
}
