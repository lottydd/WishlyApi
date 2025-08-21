package com.lotty.wishlysystemapi.mapper;


import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")

public interface ItemMapper {

    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "inWishlists", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Item toEntity(AddItemToWishlistDTO dto);


    @Mapping(target = "itemId", ignore = true) // ID не обновляем!
    @Mapping(target = "owner", ignore = true) // Владельца не меняем!
    @Mapping(target = "inWishlists", ignore = true) // Списки не меняем!
    void updateItemFromDTO(UpdateItemDTO dto, @MappingTarget Item item);

    ItemCreateResponseDTO toItemCreateResponseDTO (Item item);

    ItemResponseDTO toItemResponseDTO(Item item);

    default List<ItemResponseDTO> toItemResponseDTOList(List<Item> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream().map(this::toItemResponseDTO).toList();
    }

}
