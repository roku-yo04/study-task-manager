package com.mta.studytaskmanager.modules.auth.service;

import com.mta.studytaskmanager.core.exception.BusinessException;
import com.mta.studytaskmanager.core.exception.DuplicateException;
import com.mta.studytaskmanager.core.exception.ForbiddenException;
import com.mta.studytaskmanager.core.exception.ResourceNotFoundException;
import com.mta.studytaskmanager.modules.auth.dto.AuthResponse;
import com.mta.studytaskmanager.modules.auth.dto.LoginRequest;
import com.mta.studytaskmanager.modules.auth.dto.RegisterRequest;
import com.mta.studytaskmanager.modules.role.entity.Role;
import com.mta.studytaskmanager.modules.role.enums.RoleName;
import com.mta.studytaskmanager.modules.role.repository.RoleRepository;
import com.mta.studytaskmanager.modules.user.controller.UserRepository;
import com.mta.studytaskmanager.modules.user.entity.User;
import com.mta.studytaskmanager.modules.user.enums.PlanType;
import com.mta.studytaskmanager.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    private PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        String username = normalizeUsername(request.getUserName());
        String email = normalizeEmail(request.getEmail());
        String displayName = normalizeDisplayName(request.getDisplayName());

        validateNormalizedRegisterData(username, email, displayName);

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateException("Username đã tồn tại");
        }

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateException("Email đã tồn tại");
        }

        Role userRole = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ROLE_USER trong hệ thống"));

        User user = new User();
        user.setUserName(username);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setIsActive(true);
        user.setPlanType(PlanType.FREE);
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);
        // Giai đoạn hiện tại: đăng ký xong chưa auto-login
        return toAuthResponse(savedUser, null);
    }

    public AuthResponse login(LoginRequest request) {
        String username = normalizeUsername(request.getUserName());

        if (username == null || username.isBlank()) {
            throw new BusinessException("Username không hợp lệ");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Sai username hoặc password"));

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new ForbiddenException("Tài khoản đã bị vô hiệu hóa");
        }

        boolean passwordMatched = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatched) {
            throw new BusinessException("Sai username hoặc password");
        }

        String accessToken = jwtService.generateToken(user.getUserName());

        return toAuthResponse(user, accessToken);
    }


    // mapping từ registerRequest sang AuthResponse, chỉ lấy username để tạo token, các thông tin khác sẽ được lấy từ database khi cần thiết
    private AuthResponse toAuthResponse(User user, String accessToken) {
        Set<String> roleNames = user.getRoles()
                .stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
                    // do RoleName ( 1 user có 2 role) -> dùng set.
        return AuthResponse.builder()
                .accessToken(accessToken)
                .expiresIn(accessToken != null ? jwtService.getExpirationInSeconds() : null)
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .isActive(user.getIsActive())
                .plan(user.getPlanType().name())
                .maxTasks(user.getPlanType().getMaxTasks())
                .maxCategories(user.getPlanType().getMaxCategories())
                .roles(roleNames)
                .build();
    }

    // dùng để kiểm tra dữ liệu đã được chuẩn hóa có hợp lệ hay không,
    // nếu không hợp lệ sẽ ném ra BusinessException với thông báo lỗi cụ thể
    private void validateNormalizedRegisterData(String userName,String email,String displayName) {
        if (userName == null || userName.isBlank()) {
            throw new BusinessException("Username không hợp lệ");
        }
        if (email == null || email.isBlank()) {
            throw new BusinessException("Email không hợp lệ");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new BusinessException("Display name không hợp lệ");
        }
        if (displayName.length() < 2 || displayName.length() > 100) {
            throw new BusinessException("Display name phải có từ 2 đến 100 ký tự");
        }
    }


    // chuẩn hóa username đúng định dạng, loại bỏ khoảng trắng, chuyển về chữ thường
    private String normalizeUsername(String username) {
        if (username == null) {
            return null;
        }
        return username.trim().toLowerCase();
    }
    // chuẩn hóa email đúng định dạng, loại bỏ khoảng trắng, chuyển về chữ thường
    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
    private String normalizeDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }

        String cleaned = displayName.trim().replaceAll("\\s+", " ");
        return cleaned.isBlank() ? null : cleaned;
    }

}
