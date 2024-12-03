package com.shopr.catalog.Util;

import com.shopr.catalog.dtos.ImageDto;
import com.shopr.catalog.model.ProductImage;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Base64;

@Service
public class ImageUtil {
    public ImageDto convertImageToImagDto(ProductImage productImage) {
        ImageDto imageDto = new ImageDto();
        if(productImage!=null){
            try {
                byte[] imageBytes = productImage.getImage().getBytes(1, (int) productImage.getImage().length());
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                imageDto.setImage("data:" + productImage.getFileType() + ";base64," + base64Image);
                imageDto.setThumbnail(productImage.isThumbnail());
                imageDto.setFileName(productImage.getFileName());
                imageDto.setId(productImage.getId());

            } catch (SQLException e) {
                e.printStackTrace();
                // Handle SQL exception as needed
            }

        }
        return imageDto;
    }
}
