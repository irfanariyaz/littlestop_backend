package com.shopr.catalog.model;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.*;


import java.sql.Blob;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private Blob image;
    private String fileName;
    private  String fileType;
    private boolean isThumbnail;
    //private  String downloadUrl;
//    private int displayOrder;

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

}
