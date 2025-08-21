package com.lotty.wishlysystemapi.model;

import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "Items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @Column(nullable = false)
    private String itemName;

    private String description;

    @Column(nullable = false)
    private Double price;

    private String imageURL;

    @Column(nullable = false)
    private String sourceURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToMany(mappedBy = "wishlistItems")
    private List<Wishlist> inWishlists = new ArrayList<>();



}