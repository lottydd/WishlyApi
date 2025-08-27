package com.lotty.wishlysystemapi.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ при создании пользователя")
public class UserCreateResponseDTO {
    @Schema(description = "ID пользователя", example = "1")
    private Integer userId;

    @Schema(description = "Имя пользователя", example = "john")
    private String username;

    @Schema(description = "Email пользователя", example = "john@example.com")
    private String email;

    @Schema(description = "Описание пользователя", example = "Бедный программист")
    private String description;

    @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;
}
