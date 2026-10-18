package com.example.springboot.controller;

import com.example.springboot.config.JwtProvider;
import com.example.springboot.dto.UserDTO;
import com.example.springboot.service.AuthService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider; 

    private final long EXPIRATION_MS =
            24 * 60 * 60 * 1000;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody UserDTO req
    ) {

        String jwt = authService.login(
                req.getEmail(),
                req.getPassword()
        );

        if (jwt == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid credentials"
                            )
                    );
        }

        ResponseCookie cookie =
                ResponseCookie.from("jwt", jwt)
                        .httpOnly(true)
                        .path("/")
                        .maxAge(EXPIRATION_MS / 1000)
                        .build();

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        cookie.toString()
                )
                .body(
                        Map.of(
                                "email",
                                req.getEmail()
                        )
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response
    ) {

        ResponseCookie cookie =
                ResponseCookie.from("jwt", "")
                        .path("/")
                        .httpOnly(true)
                        .maxAge(0)
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/signin")
    public ResponseEntity<?> register(
            @RequestBody UserDTO req
    ) {

        boolean result =
                authService.signin(
                        req.getEmail(),
                        req.getPassword()
                );

        if (!result) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "message",
                                    "Email already exists"
                            )
                    );
        }

        return ResponseEntity
                .ok(
                        Map.of(
                                "success",
                                true
                        )
                );
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkLoginStatus(
            HttpServletRequest request
    ) {

        try {

            String token = null;

            if (request.getCookies() != null) {

                for (Cookie cookie :
                        request.getCookies()) {

                    if ("jwt".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token == null) {
                return ResponseEntity.ok(
                        Map.of("loggedIn", false)
                );
            }
            
            jwtProvider.parseToken(token);


            return ResponseEntity.ok(
                    Map.of("loggedIn", true)
            );

        } catch (Exception e) {

            return ResponseEntity.ok(
                    Map.of("loggedIn", false)
            );
        }
    }
}
