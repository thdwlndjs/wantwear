package com.example.springboot.dto;

import lombok.*;

@Getter

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    private String category;
    private String url;

    private String ogImage;
    private String ogTitle;

    private Integer price;
    private Boolean isSoldOut;
    

}
