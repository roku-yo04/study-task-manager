package com.mta.studytaskmanager.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AuthResponse {
    // dùng để trả về token sau khi đăng nhập thành công, hoặc thông tin người dùng sau khi đăng ký thành công
    // PHẦN 1: JWT TOKEN
    private String accessToken; // token JWT được tạo ra sau khi đăng nhập thành công
    @Builder.Default
    private String tokenType = "Bearer"; // loại token, thường là "bearer"
    private Long expiresIn; // thời gian hết hạn của token (tính bằng giây)
    private String refreshToken; // có thể tạm thời null nếu chưa làm refresh

    // PHẦN 2: Thông tin để hiển thị UI
    private Long id;
    private String userName;
    private String email;
    private String displayName;
    private Boolean isActive;
    // PLAN
    private String plan; // khi xác thực xong sẽ hiện thị UI theo loại user luôn.
    private Integer maxTasks;      // Trả về số lượng giới hạn
    private Integer maxCategories; // Trả về số lượng giới hạn

    // PHẦN 3: PHÂN QUYỀN
    private Set<String> roles;

}
