package com.mta.studytaskmanager.core.config;

import com.mta.studytaskmanager.modules.role.entity.Role;
import com.mta.studytaskmanager.modules.role.enums.RoleName;
import com.mta.studytaskmanager.modules.role.repository.RoleRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// CommandLineRunner nên sau khi app khởi động xong, Spring sẽ tự gọi method run().
public class DataSeeder implements CommandLineRunner {
    private final RoleRepository roleRepository;


    @Override
    public void run(String... args) throws Exception {
        seedRole(RoleName.ROLE_USER);
        seedRole(RoleName.ROLE_ADMIN);
        seedRole(RoleName.ROLE_MODERATOR);
        
    }

    private void seedRole(RoleName roleName) {
        // BƯỚC 1: KIỂM TRA (Idempotent)
        if (!roleRepository.existsByRoleName(roleName)) {

            // BƯỚC 2: TẠO MỚI (Nếu chưa có)
            Role role = new Role();
            role.setRoleName(roleName);

            // BƯỚC 3: LƯU VÀO DATABASE
            roleRepository.save(role);
        }
        // NẾU CÓ RỒI THÌ THÔI, KHÔNG LÀM GÌ CẢ -> GIÚP APP KHÔNG BỊ LỖI TRÙNG DỮ LIỆU
    }


}
