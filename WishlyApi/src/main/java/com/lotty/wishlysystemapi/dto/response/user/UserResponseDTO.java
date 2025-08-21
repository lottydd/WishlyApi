package com.lotty.wishlysystemapi.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO с информацией о пользователе")
public class UserResponseDTO {
    @Schema(description = "ID пользователя", example = "1")
    private Integer id;

    @Schema(description = "Имя пользователя", example = "john")
    private String username;

    @Schema(description = "Email пользователя", example = "john@example.com")
    private String email;

    @Schema(description = "Описание пользователя", example = "Бедный программист")
    private String description;

    @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;
}