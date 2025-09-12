package com.lotty.wishlysystemapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Wishlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wishlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer wishlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    @Column(nullable = false)
    private String wishlistName;

    private String wishlistDescription;

    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "WishlistItems",
            joinColumns = @JoinColumn(name = "wishlistId"),
            inverseJoinColumns = @JoinColumn(name = "itemId")
    )
    private List<Item> wishlistItems = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    // Автоматически вычисляемое количество
    public int getItemCount() {
        return wishlistItems.size();
    }
}