package com.example.springboot.repository;

import com.example.springboot.entity.ProductEntity;
import com.example.springboot.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	List<ProductEntity> findAllByUserOrderByCreatedAtDesc(UserEntity user);
	Optional<ProductEntity> findByIdAndUser(Long id, UserEntity user);
}
