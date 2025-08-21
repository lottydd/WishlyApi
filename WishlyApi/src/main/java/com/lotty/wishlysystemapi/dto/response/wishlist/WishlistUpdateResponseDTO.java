package com.lotty.wishlysystemapi.dto.response.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Ответ при обновлении вишлиста")
public class WishlistUpdateResponseDTO {
        @Schema(description = "ID вишлиста", example = "101")
        private Integer wishlistId;

        @Schema(description = "Название вишлиста", example = "Обновленный список подарков")
        private String wishlistName;

        @Schema(description = "Описание вишлиста", example = "Новый список к дню рождения")
        private String wishlistDescription;

        @Schema(description = "Дата изменения", example = "2025-08-21T15:30:00")
        private LocalDateTime modifiedDate;

        @Schema(description = "Количество вещей", example = "7")
        private Integer itemCount;
}
