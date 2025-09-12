package com.lotty.wishlysystemapi.dto.request.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос для обновления информации о вещи в вишлисте")
public class UpdateItemDTO {
    @Schema(description = "Описание вещи", example = "Ноутбук, 16GB RAM")
    private String description;

    @Schema(description = "ID вещи", example = "201", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemId;

    @Schema(description = "Название вещи", example = "MacBook Pro")
    private String itemName;

    @Schema(description = "Ссылка на источник", example = "http://store.com/item/123")
    private String sourceURL;

    @Schema(description = "Цена вещи", example = "1499.99")
    private Double price;

    @Schema(description = "URL изображения вещи", example = "http://example.com/macbook.jpg")
    private String imageURL;
}
