package com.example.springboot.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SoldOutChecker {

    private static final String UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
        "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    public boolean check(String url) {
        try {
            Document doc = Jsoup.connect(url)
                .userAgent(UA)
                .timeout(7000)
                .followRedirects(true)
                .get();

            return isSoldOut(doc);
        } catch (Exception e) {
            throw new RuntimeException("품절 체크 실패: " + url, e);
        }
    }

    private boolean isSoldOut(Document doc) {
        doc.select("script, style, noscript").remove();

        // 1) alt에 '품절'
        if (!doc.select("img[alt*=품절], area[alt*=품절], input[alt*=품절]").isEmpty()) return true;

        // 2) 클래스 기반 soldout류 + title/text '품절'
        Elements elems = doc.select(
            "[class*=soldout], [class*=sold-out], [class*=outofstock], [class*=no-stock], " +
            "[class*=품절], [class*=재고없음]"
        );
        for (Element el : elems) {
            String t = el.attr("title");
            String text = el.text();
            if ((t != null && t.contains("품절")) || (text != null && text.contains("품절"))) return true;
        }

        // 3) option 텍스트 '품절'
        for (Element opt : doc.select("option")) {
            if (opt.text().contains("품절")) return true;
        }

        // 4) 페이지 텍스트에 '품절' / '재입고 알림'
        String allText = doc.text();
        if (allText.contains("재입고 알림")) return true;
        if (allText.contains("품절")) return true;

        // 5) alt '재입고 알림'
        if (!doc.select("img[alt*=재입고 알림]").isEmpty()) return true;

        return false;
    }
}
