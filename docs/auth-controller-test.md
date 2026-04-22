# AuthController Test - Unit & Integration Tests

## 📋 Mục tiêu

Test 2 endpoints của AuthController:
- `POST /api/auth/register` - Đăng ký user mới
- `POST /api/auth/login` - Đăng nhập

---

## 🗂️ Cấu trúc file test

```
backend/src/test/java/com/mta/studytaskmanager/modules/auth/controller/
└── AuthControllerTest.java
```

---

## 🧪 AuthControllerTest.java

**Location:** `backend/src/test/java/com/mta/studytaskmanager/modules/auth/controller/AuthControllerTest.java`

```java
package com.mta.studytaskmanager.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Sử dụng @WebMvcTest để test riêng Controller layer
 * Mock AuthService để không phụ thuộc vào database
 */
@WebMvcTest(AuthController.class)
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
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }
}
```

---

## 🎯 Giải thích code test

### 1. **Annotations**

```java
@WebMvcTest(AuthController.class)
```
- Test riêng Controller layer
- Không load toàn bộ Spring context (nhanh hơn)
- Chỉ load AuthController và các components liên quan

```java
@MockBean
private AuthService authService;
```
- Mock AuthService để không phụ thuộc database
- Giả lập response từ service

```java
@DisplayName("...")
```
- Mô tả test case dễ đọc
- Hiển thị trong test report

### 2. **Setup (@BeforeEach)**

```java
@BeforeEach
void setUp() {
    // Chuẩn bị data cho mỗi test case
    validRegisterRequest = new RegisterRequest();
    // ...
}
```

- Chạy trước mỗi test method
- Chuẩn bị test data
- Đảm bảo mỗi test độc lập

### 3. **Test Structure (Given-When-Then)**

```java
@Test
void testRegister_Success() throws Exception {
    // Given: Chuẩn bị điều kiện
    when(authService.register(any(RegisterRequest.class)))
            .thenReturn(mockAuthResponse);

    // When: Thực hiện action
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRegisterRequest)))

    // Then: Verify kết quả
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
}
```

### 4. **MockMvc Methods**

```java
mockMvc.perform(...)      // Thực hiện HTTP request
    .andDo(print())       // In request/response ra console
    .andExpect(...)       // Verify response
```

### 5. **JsonPath Assertions**

```java
.andExpect(jsonPath("$.success").value(true))
.andExpect(jsonPath("$.data.accessToken").value("mock-jwt-token-12345"))
.andExpect(jsonPath("$.data.roles[0]").value("USER"))
```

- `$` = root object
- `.` = access property
- `[0]` = access array element

---

## 🏃 Cách chạy tests

### Chạy tất cả tests:

```bash
mvn test
```

### Chạy riêng AuthControllerTest:

```bash
mvn test -Dtest=AuthControllerTest
```

### Chạy 1 test method cụ thể:

```bash
mvn test -Dtest=AuthControllerTest#testRegister_Success
```

### Trong IntelliJ IDEA:

1. Right-click vào `AuthControllerTest.java`
2. Chọn "Run 'AuthControllerTest'"
3. Hoặc click vào icon ▶️ bên cạnh class/method

---

## 📊 Test Coverage

Test này cover:

### Register endpoint:
- ✅ Success case với valid data
- ✅ Validation: username trống, quá ngắn, quá dài
- ✅ Validation: email trống, không hợp lệ
- ✅ Validation: password trống, quá ngắn
- ✅ Validation: displayName trống, quá ngắn
- ✅ Edge case: request body null

### Login endpoint:
- ✅ Success case với valid credentials
- ✅ Validation: username trống, null
- ✅ Validation: password trống, null
- ✅ Edge case: request body trống
- ✅ Edge case: Content-Type sai

**Tổng: 18 test cases**

---

## 🔍 Expected Test Results

Khi chạy tests, bạn sẽ thấy:

```
AuthController Tests
  ✓ Register - Success với valid data
  ✓ Register - Fail khi username trống
  ✓ Register - Fail khi username quá ngắn (< 3 ký tự)
  ✓ Register - Fail khi username quá dài (> 20 ký tự)
  ✓ Register - Fail khi email không hợp lệ
  ✓ Register - Fail khi email trống
  ✓ Register - Fail khi password quá ngắn (< 6 ký tự)
  ✓ Register - Fail khi password trống
  ✓ Register - Fail khi displayName trống
  ✓ Register - Fail khi displayName quá ngắn (< 3 ký tự)
  ✓ Register - Fail khi request body null
  ✓ Login - Success với valid credentials
  ✓ Login - Fail khi username trống
  ✓ Login - Fail khi password trống
  ✓ Login - Fail khi username null
  ✓ Login - Fail khi password null
  ✓ Login - Fail khi request body trống
  ✓ Login - Fail khi Content-Type không phải JSON

Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
```

---

## 🐛 Troubleshooting

### Issue 1: Cannot resolve MockMvc

**Giải pháp:** Thêm dependency vào `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Issue 2: Test fails với "No qualifying bean"

**Giải pháp:** Đảm bảo đã mock tất cả dependencies:

```java
@MockBean
private AuthService authService;
```

### Issue 3: JsonPath not found

**Giải pháp:** Check response structure bằng `.andDo(print())`

---

## 📝 Notes

- Tests này là **Unit Tests** (test riêng Controller)
- Không connect database (dùng @MockBean)
- Chạy nhanh (~2-3 giây cho 18 tests)
- Phù hợp cho CI/CD pipeline
- Nên chạy trước mỗi commit

---

## 🎯 Next Steps

Sau khi test Controller, bạn có thể:

1. **Test Service Layer** (AuthServiceTest)
2. **Test Repository Layer** (UserRepositoryTest)
3. **Integration Tests** (test toàn bộ flow với database thật)
4. **Test Security** (JWT validation, authorization)

---

**🎉 Chúc bạn test thành công!**
