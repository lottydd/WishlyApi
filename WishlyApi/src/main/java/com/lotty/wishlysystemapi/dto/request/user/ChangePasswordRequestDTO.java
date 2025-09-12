package com.lotty.wishlysystemapi.dto.request.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос для смены пароля")
public class ChangePasswordRequestDTO {

    private String oldPassword;
    private String newPassword;

}