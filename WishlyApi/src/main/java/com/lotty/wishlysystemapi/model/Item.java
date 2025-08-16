package com.lotty.wishlysystemapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@Table(name = "Items")
@AllArgsConstructor
@NoArgsConstructor

public class Item {
    private Integer itemId;
    private String name;
    private String description;
    private double price;
    private String imageUrl; // Ссылка на изображение товара
    private String link; // Ссылка на товар
}
