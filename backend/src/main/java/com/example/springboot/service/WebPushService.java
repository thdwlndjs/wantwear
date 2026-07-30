package com.example.springboot.service;

import com.example.springboot.entity.ProductEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebPushService {

    public void sendRestock(ProductEntity product) {
        // 🔔 나중에 실제 Web Push 코드로 교체
        log.info("[PUSH] 재입고 알림 발송 예정 - title={}, url={}",
                product.getOgTitle(), product.getUrl());
    }
}
