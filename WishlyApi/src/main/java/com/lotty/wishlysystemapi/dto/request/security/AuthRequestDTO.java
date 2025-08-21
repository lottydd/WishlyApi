package com.lotty.wishlysystemapi.dto.request.security;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос для авторизации")


public class AuthRequestDTO {
    @Schema(description = "Имя пользователя", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Пароль пользователя", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
