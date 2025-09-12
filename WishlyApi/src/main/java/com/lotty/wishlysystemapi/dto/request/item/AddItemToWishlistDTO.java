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
@Schema(description = "Запрос для добавления вещи в вишлист")
public class AddItemToWishlistDTO {

    @Schema(description = "ID вишлиста", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer wishlistId;

    @Schema(description = "Описание айтем", example = "Наушники с шумоподавлением")
    private String description;

    @Schema(description = "Ссылка на айтем", example = "http://store.com/item/321")
    private String sourceURL;

    @Schema(description = "Цена айтем", example = "199.99")
    private Double price;

    @Schema(description = "Название айтем", example = "Sony WH-1000XM5")
    private String itemName;

    @Schema(description = "URL изображения айтем", example = "http://example.com/sony.jpg")
    private String imageURL;
}
