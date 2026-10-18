package com.example.springboot.service;

import com.example.springboot.config.JwtProvider;
import com.example.springboot.entity.UserEntity;
import com.example.springboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    // 회원가입
    public boolean signin(String email, String password) {

        if (userRepository.findByEmail(email) != null) {
            return false;
        }

        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return true;
    }

    // 로그인
    public String login(String email, String password) {

        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        return jwtProvider.createToken(email);
    }

    public String loginWithOAuth2(String email) {
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            user = new UserEntity();
            user.setEmail(email);
            user.setPassword(null);
            userRepository.save(user);
        }

        return jwtProvider.createToken(email);
    }
}
