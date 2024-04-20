package com.lotty.wishlysystemapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wishlist {
    private Long id;
    private String name;
    private List<Item> items;
    private String description;
    private String type;
    private String imageUrl;
    private int itemCount;
}
