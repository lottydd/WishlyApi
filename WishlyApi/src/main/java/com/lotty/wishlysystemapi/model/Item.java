package com.lotty.wishlysystemapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "Items")
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
    private double price;

    @Column(nullable = false, unique = true)
    private String imageURL;

    @Column(nullable = false, unique = true)
    private String sourceURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_list_id", nullable = false)
    private ItemList itemList;
}