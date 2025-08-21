package com.lotty.wishlysystemapi.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ при обновлении пользователя")
public class UserUpdateResponseDTO {
    @Schema(description = "ID пользователя", example = "1")
    private Integer userId;

    @Schema(description = "Имя пользователя", example = "john")
    private String username;

    @Schema(description = "Email пользователя", example = "john@example.com")
    private String email;

    @Schema(description = "Описание пользователя", example = "Бедный программист")
    private String description;
}
