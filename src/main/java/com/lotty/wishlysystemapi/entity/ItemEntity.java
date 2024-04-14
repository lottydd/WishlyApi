package com.lotty.wishlysystemapi.entity;

import jakarta.persistence.*;

import lombok.Data;

@Entity
@Table(name = "items")
@Data
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private double price;

    @Column(name = "image_url")
    private String imageUrl;

    private int quantity;
    }