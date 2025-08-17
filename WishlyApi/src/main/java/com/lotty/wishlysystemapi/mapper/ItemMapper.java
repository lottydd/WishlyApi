package com.lotty.wishlysystemapi.mapper;


import com.lotty.wishlysystemapi.dto.request.wishlist.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface ItemMapper {

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "itemId", ignore = true)
    Item toEntity(AddItemToWishlistDTO dto);

}
