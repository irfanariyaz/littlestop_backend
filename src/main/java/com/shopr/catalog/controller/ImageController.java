package com.shopr.catalog.controller;

import com.shopr.catalog.dtos.ImageDto;
import com.shopr.catalog.exceptions.ResourceNotFoundException;
import com.shopr.catalog.model.Product;
import com.shopr.catalog.model.ProductImage;
import com.shopr.catalog.repo.ProductRepository;
import com.shopr.catalog.response.ApiResponse;
import com.shopr.catalog.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("${api.prefix}/images")
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final IImageService imageService;
    private final ProductRepository productRepository;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile> files, @RequestParam Long productId ,@RequestParam(required = false) Integer thumbnail) {
        try {
           Optional<Product> product = productRepository.findById(productId);
           if(product.isEmpty()){
               return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product not found", null));
           }
            List<ProductImage> imageDtos = imageService.saveImages(files,product.get(),thumbnail);
            return ResponseEntity.ok(new ApiResponse("Upload success", imageDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Upload failed", e.getMessage()));
        }
    }
    //get all images with productId
    @GetMapping("/images/{productId}")
    public ResponseEntity<ApiResponse> getImagesByProductId(@PathVariable Long productId){
        log.info("request reached to get images of a product");
        try {
            List<ImageDto> imageDtos = imageService.getImagesByProductId(productId);
            return ResponseEntity.ok(new ApiResponse("Image get success", imageDtos));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("/image/download/{imageId}")
    public  ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        ProductImage image = imageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1,(int)image.getImage().length()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @PutMapping("/image/{imageId}/update")
    public  ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestBody MultipartFile file){
        try {
            ProductImage image = imageService.getImageById(imageId);
            if(image != null){
           ImageDto  imagedto =  imageService.updateImage(imageId,file);
                return ResponseEntity.ok(new ApiResponse("Update success", imagedto));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Update failed", null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Update failed", INTERNAL_SERVER_ERROR));
    }

    @DeleteMapping("/image/{imageId}/update")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId){
        try {
            ProductImage image = imageService.getImageById(imageId);
            if(image != null){
                imageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiResponse("Delete success", image));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Delete failed", null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Delete failed", INTERNAL_SERVER_ERROR));
    }
}
