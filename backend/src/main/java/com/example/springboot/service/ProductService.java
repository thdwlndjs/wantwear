package com.example.springboot.service;

import com.example.springboot.dto.ProductDTO;
import com.example.springboot.entity.ProductEntity;
import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.ProductRepository;
import com.example.springboot.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SoldOutChecker soldOutChecker;
    private final UserRepository userRepository;

    private UserEntity currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthenticated");
        }

        UserEntity user = userRepository.findByEmail(auth.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> findAllDto() {
        UserEntity user = currentUser();
        return productRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDTO findDtoById(Long id) {
        UserEntity user = currentUser();
        return productRepository.findByIdAndUser(id, user)
                .map(this::toDto)
                .orElse(null); // 원하면 NOT_FOUND로 바꾸는 것도 추천
    }

    // 추가: OG는 DTO로 받되, soldOut만 서버에서 채움
    @Transactional
    public ProductDTO create(ProductDTO req) {
        boolean soldOut = soldOutChecker.check(req.getUrl());
        UserEntity user = currentUser();
        
        ProductEntity entity = ProductEntity.builder()
        		.user(user)
                .category(req.getCategory())
                .url(req.getUrl())
                .ogImage(req.getOgImage())
                .ogTitle(req.getOgTitle())
                .price(req.getPrice())
                .soldOut(soldOut)
                .build();

        return toDto(productRepository.save(entity));
    }

    @Transactional
    public void deleteById(Long id) {
    UserEntity user = currentUser();
    ProductEntity p = productRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    productRepository.delete(p);    }

    // 스케줄러: 품절만 갱신(OG/Title은 손대지 않음)
    @Transactional
    public void refreshSoldOutAll() {
        List<ProductEntity> all = productRepository.findAll();
        for (ProductEntity p : all) {
            boolean now = soldOutChecker.check(p.getUrl());
            Boolean prev = p.getSoldOut();
            if (prev == null || prev != now) {
                p.updateSoldOut(now);
            }
        }
    }

    private ProductDTO toDto(ProductEntity p) {
        return new ProductDTO(
                p.getId(),
                p.getCategory(),
                p.getUrl(),
                p.getOgImage(),
                p.getOgTitle(),
                p.getPrice(),
                p.getSoldOut()
        );
    }
}
