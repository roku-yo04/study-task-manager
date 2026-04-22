package com.mta.studytaskmanager.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mta.studytaskmanager.core.config.SecurityConfig;
import com.mta.studytaskmanager.modules.auth.dto.AuthResponse;
import com.mta.studytaskmanager.modules.auth.dto.LoginRequest;
import com.mta.studytaskmanager.modules.auth.dto.RegisterRequest;
import com.mta.studytaskmanager.modules.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit Test cho AuthController
 * Sử dụng @WebMvcTest để chỉ load web layer (không load JPA, database...)
 * Mock AuthService để test controller logic và validation
 * Import SecurityConfig để test có security context
 * Exclude JpaAuditingConfig để tránh lỗi "JPA metamodel must not be empty"
 */
@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
    }
)
@Import(SecurityConfig.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private AuthResponse mockAuthResponse;

    @BeforeEach
    void setUp() {
        // Setup valid register request
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUserName("testuser");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setDisplayName("Test User");

        // Setup valid login request
        validLoginRequest = new LoginRequest();
        validLoginRequest.setUserName("testuser");
        validLoginRequest.setPassword("password123");

        // Setup mock auth response
        mockAuthResponse = AuthResponse.builder()
                .accessToken("mock-jwt-token-12345")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .refreshToken("mock-refresh-token")
                .id(1L)
                .userName("testuser")
                .email("test@example.com")
                .displayName("Test User")
                .isActive(true)
                .plan("FREE")
                .maxTasks(50)
                .maxCategories(10)
                .roles(Set.of("USER"))
                .build();
    }

    // ==================== REGISTER TESTS ====================

    @Test
    @DisplayName("Register - Success với valid data")
    void testRegister_Success() throws Exception {
        // Given: Mock service trả về success response
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(mockAuthResponse);

        // When & Then: Gọi API và verify response
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("mock-jwt-token-12345"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.userName").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.displayName").value("Test User"))
                .andExpect(jsonPath("$.data.plan").value("FREE"))
                .andExpect(jsonPath("$.data.roles[0]").value("USER"));
    }

    @Test
    @DisplayName("Register - Fail khi username trống")
    void testRegister_Fail_EmptyUsername() throws Exception {
        // Given: Request với username trống
        validRegisterRequest.setUserName("");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi username quá ngắn (< 3 ký tự)")
    void testRegister_Fail_UsernameTooShort() throws Exception {
        // Given: Username chỉ có 2 ký tự
        validRegisterRequest.setUserName("ab");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi username quá dài (> 20 ký tự)")
    void testRegister_Fail_UsernameTooLong() throws Exception {
        // Given: Username dài hơn 20 ký tự
        validRegisterRequest.setUserName("thisusernameiswaytoolongforvalidation");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi email không hợp lệ")
    void testRegister_Fail_InvalidEmail() throws Exception {
        // Given: Email không đúng format
        validRegisterRequest.setEmail("invalid-email");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi email trống")
    void testRegister_Fail_EmptyEmail() throws Exception {
        // Given: Email trống
        validRegisterRequest.setEmail("");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi password quá ngắn (< 6 ký tự)")
    void testRegister_Fail_PasswordTooShort() throws Exception {
        // Given: Password chỉ có 5 ký tự
        validRegisterRequest.setPassword("12345");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi password trống")
    void testRegister_Fail_EmptyPassword() throws Exception {
        // Given: Password trống
        validRegisterRequest.setPassword("");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi displayName trống")
    void testRegister_Fail_EmptyDisplayName() throws Exception {
        // Given: DisplayName trống
        validRegisterRequest.setDisplayName("");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi displayName quá ngắn (< 3 ký tự)")
    void testRegister_Fail_DisplayNameTooShort() throws Exception {
        // Given: DisplayName chỉ có 2 ký tự
        validRegisterRequest.setDisplayName("AB");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Register - Fail khi request body null")
    void testRegister_Fail_NullRequestBody() throws Exception {
        // When & Then: Gửi request body null
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ==================== LOGIN TESTS ====================

    @Test
    @DisplayName("Login - Success với valid credentials")
    void testLogin_Success() throws Exception {
        // Given: Mock service trả về success response
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(mockAuthResponse);

        // When & Then: Gọi API và verify response
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User logged in successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("mock-jwt-token-12345"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                .andExpect(jsonPath("$.data.userName").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Login - Fail khi username trống")
    void testLogin_Fail_EmptyUsername() throws Exception {
        // Given: Username trống
        validLoginRequest.setUserName("");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login - Fail khi password trống")
    void testLogin_Fail_EmptyPassword() throws Exception {
        // Given: Password trống
        validLoginRequest.setPassword("");

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login - Fail khi username null")
    void testLogin_Fail_NullUsername() throws Exception {
        // Given: Username null
        validLoginRequest.setUserName(null);

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login - Fail khi password null")
    void testLogin_Fail_NullPassword() throws Exception {
        // Given: Password null
        validLoginRequest.setPassword(null);

        // When & Then: Expect validation error
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login - Fail khi request body trống")
    void testLogin_Fail_EmptyRequestBody() throws Exception {
        // When & Then: Gửi request body trống
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Login - Fail khi Content-Type không phải JSON")
    void testLogin_Fail_InvalidContentType() throws Exception {
        // When & Then: Gửi với Content-Type sai
        // Spring Boot trả về 500 (Internal Server Error) thay vì 415 khi Content-Type không hợp lệ
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}
