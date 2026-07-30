package com.example.springboot.controller;

import com.example.springboot.service.SoldOutChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/soldout")
@RequiredArgsConstructor
public class SoldOutController {

    private final SoldOutChecker soldOutChecker;

    @GetMapping("/check")
    public ResponseEntity<?> check(@RequestParam String url) {

        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "url query parameter is required"));
        }

        try {
            boolean isSoldOut = soldOutChecker.check(url);
            // 프론트에서 soldOutRes.data?.isSoldOut 로 쓰기 좋게 키 고정
            return ResponseEntity.ok(Map.of(
                    "url", url,
                    "isSoldOut", isSoldOut
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Failed to check sold-out",
                            "url", url
                    ));
        }
    }
}
