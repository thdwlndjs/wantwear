package com.example.springboot.scheduler;

import com.example.springboot.entity.ProductEntity;
import com.example.springboot.repository.ProductRepository;
import com.example.springboot.service.SoldOutChecker;
import com.example.springboot.service.WebPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final ProductRepository productRepository;
    private final SoldOutChecker soldOutChecker;
    private final WebPushService webPushService; // 지금은 Stub 가능

    /**
     * 60초마다 전체 상품 품절 상태 체크
     */
    @Scheduled(fixedDelayString = "${scheduler.soldout.interval-ms:60000}")
    @Transactional
    public void checkSoldOutAndNotify() {
        List<ProductEntity> products = productRepository.findAll();

        for (ProductEntity p : products) {
            Boolean prev = p.getSoldOut();
            boolean now = soldOutChecker.check(p.getUrl());

            // 🔥 재입고 감지 (true → false)
            if (Boolean.TRUE.equals(prev) && !now) {
                log.info("[RESTOCK] 재입고 감지 - productId={}, url={}", p.getId(), p.getUrl());

                // 👉 여기서 푸시 발송 (앱 꺼져 있어도 OK)
                webPushService.sendRestock(p);
            }

            // 상태 변경 시에만 DB 갱신
            if (prev == null || prev != now) {
                p.updateSoldOut(now);
            }
        }
    }
}
