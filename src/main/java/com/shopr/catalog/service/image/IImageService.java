package com.shopr.catalog.service.image;

import com.shopr.catalog.dtos.ImageDto;
import com.shopr.catalog.model.Product;
import com.shopr.catalog.model.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    ProductImage getImageById(Long id);
    void deleteImageById(Long id);
    List<ProductImage> saveImages(List<MultipartFile> files, Product Product, int thumbnail);
    ImageDto updateImage(Long imageId, MultipartFile image);

    ImageDto convertImageToDto(ProductImage savedImage);


    List<ImageDto> getImagesByProductId(Long productId);
}
