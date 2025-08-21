package com.lotty.wishlysystemapi.dto.response.item;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO с информацией о вещи")
public class ItemResponseDTO {
    @Schema(description = "Название вещи", example = "iPhone 15")
    private String itemName;

    @Schema(description = "Описание вещи", example = "128GB, черный цвет")
    private String description;

    @Schema(description = "Цена вещи", example = "999.99")
    private Double price;

    @Schema(description = "URL изображения вещи", example = "http://example.com/image.jpg")
    private String imageURL;

    @Schema(description = "Источник вещи", example = "http://store.com/item/123")
    private String sourceURL;
}
