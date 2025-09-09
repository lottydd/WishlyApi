package com.lotty.wishlysystemapi.dto.response.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Ответ при создании вещи")
public class ItemCreateResponseDTO {
    @Schema(description = "ID вещи", example = "201")
    private Integer itemId;

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
