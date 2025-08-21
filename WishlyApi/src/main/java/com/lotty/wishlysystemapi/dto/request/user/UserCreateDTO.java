package com.lotty.wishlysystemapi.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос для создания нового пользователя")
public class UserCreateDTO {
    @Schema(description = "Имя пользователя", example = "johnCoolGuy", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 5, max = 32)
    @NotBlank(message = "Username не может быть пустым")
    private String username;

    @Schema(description = "Email пользователя", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат почты")
    private String email;

    @Schema(description = "Описание профиля пользователя", example = "Вишлисты бедного программиста")
    private String description;

    @Size(min = 8, max = 32)
    @Schema(description = "Пароль пользователя", example = "securePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
