package com.mta.studytaskmanager.core.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Để mọi API trả về cùng 1 format, frontend đỡ rối.
// -> kieu generic, có thể trả về bất kỳ kiểu dữ liệu nào, tùy vào T
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // case: muốn rõ message.
    // static để có thể gọi trực tiếp mà không cần tạo instance của ApiResponse,
    // ví dụ: ApiResponse.success(data, "Task created successfully")
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }
    //case : chỉ cần trả về data thôi.
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    public static <T> ApiResponse<T> fail(String message,T data) {
        return new ApiResponse<>(false,message,data);
    }
}

