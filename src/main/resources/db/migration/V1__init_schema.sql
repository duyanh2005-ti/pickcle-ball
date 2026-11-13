-- V1: Schema ban đầu, lấy đúng theo DB pickleball đang chạy (11/2026).
-- DB đã có sẵn bảng sẽ được Flyway "baseline" ở version 1 nên file này KHÔNG chạy lại trên đó;
-- file này chỉ chạy khi tạo DB mới (máy mới, môi trường test, server mới).

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nameUser` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `court` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `court_name` varchar(255) NOT NULL,
  `type` varchar(100) DEFAULT NULL,
  `description` text,
  `city` varchar(100) DEFAULT NULL,
  `district` varchar(100) DEFAULT NULL,
  `address_detail` varchar(255) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `image` varchar(500) DEFAULT NULL,
  `owner_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_court_owner` (`owner_id`),
  CONSTRAINT `fk_court_owner` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `service` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `unit` varchar(50) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `price_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `day_of_week` varchar(50) DEFAULT NULL,
  `priceperhour` double NOT NULL,
  `court_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_price_court` (`court_id`),
  CONSTRAINT `fk_price_court` FOREIGN KEY (`court_id`) REFERENCES `court` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `booking` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `total` double DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `court_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_booking_user` (`user_id`),
  KEY `fk_booking_court` (`court_id`),
  CONSTRAINT `fk_booking_court` FOREIGN KEY (`court_id`) REFERENCES `court` (`id`),
  CONSTRAINT `fk_booking_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `booking_service` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `unit_price` double NOT NULL,
  `total` double DEFAULT NULL,
  `booking_id` bigint DEFAULT NULL,
  `service_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bs_booking` (`booking_id`),
  KEY `fk_bs_service` (`service_id`),
  CONSTRAINT `fk_bs_booking` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_bs_service` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
