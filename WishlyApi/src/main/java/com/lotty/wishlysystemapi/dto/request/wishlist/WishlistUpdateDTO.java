package com.lotty.wishlysystemapi.dto.request.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос для обновления данных вишлиста")
public class WishlistUpdateDTO {
    @Schema(description = "ID вишлиста", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer wishlistId;

    @Schema(description = "Описание вишлиста", example = "Новый список подарков")
    private String wishlistDescription;

    @Schema(description = "Название вишлиста", example = "Список ко дню рождения")
    private String wishlistName;
}
