package com.lotty.wishlysystemapi.dto.response.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Ответ при создании вишлиста")
public class WishlistCreateResponseDTO {
    @Schema(description = "ID вишлиста", example = "101")
    private Integer wishlistId;

    @Schema(description = "Название вишлиста", example = "Мой первый вишлист")
    private String wishlistName;

    @Schema(description = "Описание вишлиста", example = "Подарки к Новому Году")
    private String wishlistDescription;

    @Schema(description = "Дата создания вишлиста", example = "2025-08-21T12:00:00")
    private LocalDateTime createDate;
}
