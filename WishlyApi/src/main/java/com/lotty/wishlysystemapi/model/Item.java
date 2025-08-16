package com.lotty.wishlysystemapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;
    private String name;
    private String description;
    private double price;
    private String imageUrl; // Ссылка на изображение товара
    private int quantity; // Количество товара в наличии
    private String link; // Ссылка на товар
}
