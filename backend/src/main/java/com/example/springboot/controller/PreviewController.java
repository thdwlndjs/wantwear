package com.example.springboot.controller;

import com.example.springboot.dto.ProductDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PreviewController {

    @GetMapping("/preview")
    public ResponseEntity<?> getPreview(@RequestParam String url) {

        if (url.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "url query parameter is required"));
        }

        try {
            Document doc = Jsoup
                    .connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .followRedirects(true)
                    .get();

            String ogImage = null;
            String ogTitle = null;

            Element ogImageTag = doc.selectFirst("meta[property=og:image]");
            if (ogImageTag != null) ogImage = ogImageTag.attr("content");

            Element ogTitleTag = doc.selectFirst("meta[property=og:title]");
            if (ogTitleTag != null) ogTitle = ogTitleTag.attr("content");

            ProductDTO response = new ProductDTO();
            response.setUrl(url);
            response.setOgImage(ogImage);
            response.setOgTitle(ogTitle);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch URL"));
        }
    }
}
