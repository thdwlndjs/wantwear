package com.example.springboot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)    
    private UserEntity user;
    
    @Column(nullable = false)
    private String category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "og_image")
    private String ogImage;

    @Column(name = "og_title")
    private String ogTitle;

    private Integer price;

    @Column(name = "is_sold_out")
    private Boolean soldOut;

    @Column(name="updated_at", insertable = false, updatable = false)
    private java.sql.Timestamp updatedAt;

    @Column(name="created_at", insertable = false, updatable = false)
    private java.sql.Timestamp createdAt;

    // 스케줄러에서 품절만 갱신
    public void updateSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }
}

