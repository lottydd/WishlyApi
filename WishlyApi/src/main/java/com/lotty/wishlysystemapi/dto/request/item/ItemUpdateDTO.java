package com.lotty.wishlysystemapi.dto.request.item;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос для обновления информации о вещи")
public class ItemUpdateDTO {

    @Schema(description = "Описание вещи", example = "Ноутбук с 32GB RAM")
    private String description;

    @Schema(description = "Ссылка на источник вещи", example = "http://store.com/item/456")
    private String sourceURL;

    @Schema(description = "Цена вещи", example = "1499.99")
    private Double price;

    @Schema(description = "Название вещи", example = "MacBook Pro 16")
    private String itemName;

    @Schema(description = "URL изображения вещи", example = "http://example.com/macbook.jpg")
    private String imageURL;
}
