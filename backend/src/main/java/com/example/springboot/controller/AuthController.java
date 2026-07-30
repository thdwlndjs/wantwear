package com.example.springboot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.springboot.dto.UserDTO;
import com.example.springboot.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	@Value("${jwt.secret}")
	private String JWT_SECRET;
    private final AuthService authService;
    private final long EXPIRATION_MS = 24 * 60 * 60 * 1000;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO req) {
        String token = authService.login(req.getEmail(), req.getPassword());
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid credentials"));
        }

        String jwt = authService.generateToken(req.getEmail());

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .path("/")
                .maxAge(EXPIRATION_MS / 1000)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("email", req.getEmail()));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .path("/")
                .httpOnly(true)
                .maxAge(0) // 즉시 삭제
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> register(@RequestBody UserDTO req) {
        boolean result = authService.signin(req.getEmail(), req.getPassword());
        if (!result) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already exists"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkLoginStatus(HttpServletRequest request) {
        try {
            String token = null;
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token == null) return ResponseEntity.ok(Map.of("loggedIn", false));

            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

            return ResponseEntity.ok(Map.of("loggedIn", true));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }
    }
}
