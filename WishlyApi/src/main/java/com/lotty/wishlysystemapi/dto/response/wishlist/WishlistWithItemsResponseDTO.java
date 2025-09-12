package com.lotty.wishlysystemapi.dto.response.wishlist;

import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO с полной информацией о вишлисте")
public class WishlistWithItemsResponseDTO {

    @Schema(description = "Название вишлиста", example = "Список подарков")
    private String wishlistName;

    @Schema(description = "Описание вишлиста", example = "Список вещей для Нового Года")
    private String wishlistDescription;

    @Schema(description = "Количество вещей", example = "5")
    private Integer itemCount;

    @Schema(description = "Дата создания", example = "2025-08-21T12:00:00")
    private LocalDateTime createDate;

    @Schema(description = "Лист из вещей с информацией", example = "5")
    private List<ItemResponseDTO> wishlistItems;


}
