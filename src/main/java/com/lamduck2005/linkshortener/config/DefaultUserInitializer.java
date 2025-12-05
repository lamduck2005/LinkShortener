package com.lamduck2005.linkshortener.config;

import com.lamduck2005.linkshortener.entity.ERole;
import com.lamduck2005.linkshortener.entity.Role;
import com.lamduck2005.linkshortener.entity.User;
import com.lamduck2005.linkshortener.repository.RoleRepository;
import com.lamduck2005.linkshortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Tự động tạo 2 tài khoản test mặc định khi ứng dụng khởi động:
 * - admin / admin123 (ROLE_ADMIN + ROLE_USER)
 * - user / user123 (ROLE_USER)
 * 
 * 2 tài khoản này không thể chỉnh sửa bất kỳ thông tin nào (bảo vệ trong AdminUserServiceImpl)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Tên username của 2 tài khoản test (không thể sửa)
    public static final String ADMIN_USERNAME = "admin";
    public static final String TEST_USER_USERNAME = "user";

    /**
     * Kiểm tra xem user có phải là tài khoản test (admin/user) không.
     * 
     * @param username Username cần kiểm tra
     * @return true nếu là tài khoản test, false nếu không
     */
    public static boolean isTestAccount(String username) {
        return ADMIN_USERNAME.equals(username) || TEST_USER_USERNAME.equals(username);
    }

    /**
     * Kiểm tra và throw exception nếu user là tài khoản test.
     * Dùng trong các service để bảo vệ tài khoản test khỏi bị chỉnh sửa.
     * 
     * @param username Username cần kiểm tra
     * @param operation Tên thao tác đang thực hiện (ví dụ: "đổi mật khẩu", "đổi email", "chỉnh sửa")
     * @throws IllegalArgumentException Nếu user là tài khoản test
     */
    public static void throwIfTestAccount(String username, String operation) {
        if (isTestAccount(username)) {
            throw new IllegalArgumentException(
                    String.format("Không thể %s cho tài khoản test (admin/user). Đây là tài khoản hệ thống.", operation)
            );
        }
    }

    @Override
    public void run(String... args) {
        // Chỉ chạy khi chưa có user nào (tránh tạo lại mỗi lần restart)
        if (userRepository.count() > 0) {
            log.info("Đã có user trong hệ thống, bỏ qua việc tạo default users.");
            return;
        }

        log.info("Bắt đầu tạo default users...");

        // Lấy roles
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER chưa được cấu hình trong hệ thống."));
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN chưa được cấu hình trong hệ thống."));

        // Tạo admin user
        createAdminUser(adminRole, userRole);

        // Tạo test user
        createTestUser(userRole);

        log.info("Đã tạo xong default users: admin/admin123 và user/user123");
    }

    private void createAdminUser(Role adminRole, Role userRole) {
        if (userRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
            log.info("User 'admin' đã tồn tại, bỏ qua.");
            return;
        }

        User admin = new User(
                "admin@example.com",
                ADMIN_USERNAME,
                passwordEncoder.encode("admin123") // Password: admin123
        );
        admin.setIsActive(true);

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);

        userRepository.save(admin);
        log.info("Đã tạo user admin (username: admin, password: admin123)");
    }

    private void createTestUser(Role userRole) {
        if (userRepository.findByUsername(TEST_USER_USERNAME).isPresent()) {
            log.info("User 'user' đã tồn tại, bỏ qua.");
            return;
        }

        User testUser = new User(
                "user@example.com",
                TEST_USER_USERNAME,
                passwordEncoder.encode("user123") // Password: user123
        );
        testUser.setIsActive(true);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        testUser.setRoles(userRoles);

        userRepository.save(testUser);
        log.info("Đã tạo user test (username: user, password: user123)");
    }
}

