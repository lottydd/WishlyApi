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
@Schema(description = "Запрос для удаления вещи из вишлиста")
public class RemoveItemDTO {
    private Integer userId;
    private Integer wishlistId;
    private Integer itemId;
}
