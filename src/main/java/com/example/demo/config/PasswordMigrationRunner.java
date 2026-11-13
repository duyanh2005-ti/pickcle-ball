package com.example.demo.config;

import com.example.demo.Entity.UsersEntity;
import com.example.demo.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PasswordMigrationRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        int migrated = 0;
        for (UsersEntity user : userRepository.findAll()) {
            String stored = user.getPassWord();
            if (stored != null && !isBcrypt(stored)) {
                user.setPassWord(passwordEncoder.encode(stored));
                migrated++;
            }
        }
        if (migrated > 0) {
            log.warn("Đã băm BCrypt cho {} tài khoản đang lưu mật khẩu dạng thô", migrated);
        }
    }

    private boolean isBcrypt(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
