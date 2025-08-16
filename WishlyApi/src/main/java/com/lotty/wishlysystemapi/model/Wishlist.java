package com.lotty.wishlysystemapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Wishlists")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wishlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String wishlistName;

    private String description;

    @ManyToMany
    @JoinTable(
            name = "WishlistItems",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> itemList = new ArrayList<>();

    @Column(nullable = false)
    private int itemCount;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
}

