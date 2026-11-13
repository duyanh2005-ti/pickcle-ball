package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Chuẩn hoá các giá trị trạng thái cũ trong bảng booking ("Cancelled", "CANCEL", ...)
 * về đúng tên enum BookingStatus, để truy vấn kiểm tra trùng lịch bỏ qua đúng đơn đã huỷ.
 */
@Component
public class BookingStatusMigrationRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(BookingStatusMigrationRunner.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int cancelled = entityManager.createNativeQuery(
                "update booking set status = 'CANCELLED' "
                        + "where upper(status) like 'CANCEL%' and binary status <> 'CANCELLED'")
                .executeUpdate();
        int confirmed = entityManager.createNativeQuery(
                "update booking set status = 'CONFIRMED' "
                        + "where (upper(status) like 'CONFIRM%' or upper(status) = 'APPROVED') and binary status <> 'CONFIRMED'")
                .executeUpdate();
        int pending = entityManager.createNativeQuery(
                "update booking set status = 'PENDING' "
                        + "where upper(status) = 'PENDING' and binary status <> 'PENDING'")
                .executeUpdate();
        int total = cancelled + confirmed + pending;
        if (total > 0) {
            log.warn("Đã chuẩn hoá trạng thái cho {} đơn đặt sân (CANCELLED={}, CONFIRMED={}, PENDING={})",
                    total, cancelled, confirmed, pending);
        }
    }
}
