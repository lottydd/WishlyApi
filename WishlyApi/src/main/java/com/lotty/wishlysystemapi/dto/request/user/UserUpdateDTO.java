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
@Schema(description = "Запрос для обновления данных пользователя")
public class UserUpdateDTO {
    @Size(min = 5, max = 16)
    @Schema(description = "Имя пользователя", example = "john", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username не может быть пустым")
    private String username;

    @Schema(description = "Email пользователя", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Email не может быть пустым")
    @Email
    private String email;

    @Schema(description = "Описание пользователя", example = "Люблю писать вишлисты")
    private String description;
}
