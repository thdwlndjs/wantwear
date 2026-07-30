package com.example.springboot.service;

import org.springframework.beans.factory.annotation.Value;
import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Value("${jwt.secret}")
    private String JWT_SECRET;
    private final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 1일

    // 회원가입
    public boolean signin(String email, String password) {
        if (userRepository.findByEmail(email) != null) return false;
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return true;
    }

    // 로그인
    public String login(String email, String password) {
    	UserEntity user = userRepository.findByEmail(email);
        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPassword())) return null;
        return email; // 실제 토큰은 controller에서 생성
    }

    // JWT 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                .compact();
    }
}
