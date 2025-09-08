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
@Schema(description = "Запрос для создания нового вишлиста")
public class WishlistCreateDTO {

   @Schema(description = "Название вишлиста", example = "Новый год 2025", requiredMode = Schema.RequiredMode.REQUIRED)
   private String wishlistName;

   @Schema(description = "Описание вишлиста", example = "Список подарков к празднику")
   private String wishlistDescription;
}
