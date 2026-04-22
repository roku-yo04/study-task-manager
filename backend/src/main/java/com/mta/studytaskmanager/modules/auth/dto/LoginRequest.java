package com.mta.studytaskmanager.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    // khi login ( frontend gửi lên ) chỉ cần username và password thôi,
    // còn email và displayName thì không cần

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;

}
