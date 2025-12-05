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
 * - admin / 123456 (ROLE_ADMIN + ROLE_USER)
 * - user / 123456 (ROLE_USER)
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

    public static final String ADMIN_USERNAME = "admin";
    public static final String TEST_USER_USERNAME = "user";

    public static boolean isTestAccount(String username) {
        return ADMIN_USERNAME.equals(username) || TEST_USER_USERNAME.equals(username);
    }

    public static void throwIfTestAccount(String username, String operation) {
        if (isTestAccount(username)) {
            throw new IllegalArgumentException(
                    String.format("Không thể %s cho tài khoản test (admin/user). Đây là tài khoản hệ thống.", operation)
            );
        }
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("ROLE_USER chua duoc cau hinh trong he thong."));
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN chua duoc cau hinh trong he thong."));

        createAdminUser(adminRole, userRole);
        createTestUser(userRole);
        
        log.info("Da tao xong tai khoan test: admin/123456 va user/123456");
    }

    private void createAdminUser(Role adminRole, Role userRole) {
        if (userRepository.findByUsername(ADMIN_USERNAME).isPresent()) {
            return;
        }

        User admin = new User("admin@example.com", ADMIN_USERNAME, passwordEncoder.encode("123456"));
        admin.setIsActive(true);

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(userRole);
        adminRoles.add(adminRole);
        admin.setRoles(adminRoles);

        userRepository.save(admin);
    }

    private void createTestUser(Role userRole) {
        if (userRepository.findByUsername(TEST_USER_USERNAME).isPresent()) {
            return;
        }

        User testUser = new User("user@example.com", TEST_USER_USERNAME, passwordEncoder.encode("123456"));
        testUser.setIsActive(true);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(userRole);
        testUser.setRoles(userRoles);

        userRepository.save(testUser);
    }
}

