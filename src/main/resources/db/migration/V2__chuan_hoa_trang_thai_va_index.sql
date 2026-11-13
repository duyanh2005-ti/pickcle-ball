-- V2: Chuẩn hoá trạng thái đơn đặt sân + thêm index / ràng buộc.

-- 1. Trạng thái đơn: đưa các cách viết cũ ("Cancelled", "CANCEL", "Confirmed"...) về đúng tên enum BookingStatus.
--    Dùng BINARY vì collation mặc định của MySQL không phân biệt hoa/thường.
UPDATE booking SET status = 'CANCELLED'
 WHERE UPPER(status) LIKE 'CANCEL%' AND BINARY status <> 'CANCELLED';
UPDATE booking SET status = 'CONFIRMED'
 WHERE (UPPER(status) LIKE 'CONFIRM%' OR UPPER(status) = 'APPROVED') AND BINARY status <> 'CONFIRMED';
UPDATE booking SET status = 'PENDING'
 WHERE UPPER(status) = 'PENDING' AND BINARY status <> 'PENDING';

-- 2. Tăng tốc truy vấn kiểm tra trùng lịch (court_id + khoảng thời gian).
CREATE INDEX idx_booking_court_time ON booking (court_id, start_time, end_time);

-- 3. Tên đăng nhập là duy nhất ở cả tầng DB (trước đây chỉ kiểm tra trong code, có thể trùng khi đăng ký đồng thời).
ALTER TABLE users ADD CONSTRAINT uk_users_name UNIQUE (nameUser);
