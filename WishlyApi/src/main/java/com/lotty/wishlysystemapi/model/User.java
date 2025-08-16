package com.lotty.wishlysystemapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String nickname;
    private String email;
    private String password;
    private String description;
    private String profileAvatarUrl; // Ссылка на аватар профиля пользователя
    private String profileBackgroundUrl; // Ссылка на фон профиля пользователя
}
