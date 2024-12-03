package com.shopr.catalog.service.image;

import com.shopr.catalog.Util.ImageUtil;
import com.shopr.catalog.dtos.ImageDto;
import com.shopr.catalog.exceptions.ResourceNotFoundException;
import com.shopr.catalog.model.Product;
import com.shopr.catalog.model.ProductImage;
import com.shopr.catalog.repo.ImageRepository;
import com.shopr.catalog.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final ImageUtil imageUtil;

    @Override
    public ProductImage getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No  image not found with "+id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete,() ->{
            throw new ResourceNotFoundException("No  image not found with "+id);});

    }

    @Override
    public List<ProductImage> saveImages(List<MultipartFile> files, Product product,int isThumbnail) {

        List<ProductImage> savedImages =  new ArrayList<>();
        for(int i=0;i<files.size();i++){
            MultipartFile file = files.get(i);
            try {
                ProductImage image = new ProductImage();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);
                image.setThumbnail(i == isThumbnail);
//                String buildDownloadUrl = "/api/v1/images/image/download/";
//                String downloadUrl = buildDownloadUrl + image.getId();
//                image.setDownloadUrl(downloadUrl);
                ProductImage savedImage = imageRepository.save(image);
//                savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
//                imageRepository.save(savedImage);

//                ImageDto imageDto = convertImageToDto(savedImage);
                savedImages.add(savedImage);

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImages;
    }

    @Override
    public ImageDto updateImage(Long imageId, MultipartFile file) {
        ProductImage image = getImageById(imageId);
        try {
            image.setImage(new SerialBlob(file.getBytes()));
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
           imageRepository.save(image);
           return convertImageToDto(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
    @Override
    public List<ImageDto> getImagesByProductId(Long productId) {
        List<ProductImage> images = imageRepository.findByProductId(productId);
        return images.stream().map(imageUtil::convertImageToImagDto)
                .toList();

    }
    @Override
    public ImageDto convertImageToDto(ProductImage savedImage){
        ImageDto imageDto = new ImageDto();
        imageDto.setThumbnail(savedImage.isThumbnail());
        imageDto.setFileName(savedImage.getFileName());
//        imageDto.setFileType(savedImage.getFileType());
//        imageDto.setDownloadUrl(savedImage.getDownloadUrl());
        imageDto.setId(savedImage.getId());
        return imageDto;

    }




}
