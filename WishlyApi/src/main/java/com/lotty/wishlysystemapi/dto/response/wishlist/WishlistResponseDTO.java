package com.lotty.wishlysystemapi.dto.response.wishlist;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO с полной информацией о вишлисте")
public class WishlistResponseDTO {
    @Schema(description = "ID пользователя", example = "1")
    private Integer userId;

    @Schema(description = "ID вишлиста", example = "101")
    private Integer wishlistId;

    @Schema(description = "Название вишлиста", example = "Список подарков")
    private String wishlistName;

    @Schema(description = "Описание вишлиста", example = "Список вещей для Нового Года")
    private String wishlistDescription;

    @Schema(description = "Дата создания", example = "2025-08-21T12:00:00")
    private LocalDateTime createDate;

    @Schema(description = "Дата изменения", example = "2025-08-21T15:30:00")
    private LocalDateTime modifiedDate;

    @Schema(description = "Количество вещей", example = "5")
    private Integer itemCount;
}
