package com.example.ozon_parser_wishly.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemParseResponseDTO {

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