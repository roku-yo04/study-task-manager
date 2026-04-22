package com.mta.studytaskmanager.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username phải có ít nhất 3 ký tự và tối đa 20 ký tự")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email không hợp lệ")
    @Size(max = 50, message = "Email phải có tối đa 50 ký tự")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 40, message = "Password phải có ít nhất 6 ký tự và tối đa 40 ký tự")
    private String password;

    @NotBlank(message = "Display name is required")
    @Size(min = 3, max = 50, message = "Display name tối đa 50 ký tự")
    private String displayName;



}
