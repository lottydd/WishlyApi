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
    @Schema(description = "ID пользователя", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer userId;

    @Schema(description = "ID вишлиста", example = "101", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer wishlistId;

    @Schema(description = "ID вещи", example = "201", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemId;
}
