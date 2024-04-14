package com.lotty.wishlysystemapi.entity;

import jakarta.persistence.*;
import java.util.List;

import lombok.Data;

@Entity
@Table(name = "wishlists")
@Data
public class WishlistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String type;
    @ManyToMany
    @JoinTable(name = "wishlist_items",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<ItemEntity> items;
}