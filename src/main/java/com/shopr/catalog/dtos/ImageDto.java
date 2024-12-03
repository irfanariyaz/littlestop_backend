package com.shopr.catalog.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageDto {
    private  Long id;
    private  String fileName;
    private boolean isThumbnail;
    private String image;
    public ImageDto() {
    }
}

