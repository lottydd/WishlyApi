package com.lotty.wishlysystemapi.entity;

import jakarta.persistence.*;
import java.util.List;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "profile_avatar_url")
    private String profileAvatarUrl; // Аватар профиля

    @Column(name = "profile_background_url")
    private String profileBackgroundUrl; // Фон профиля

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishlistEntity> wishlists;
}
