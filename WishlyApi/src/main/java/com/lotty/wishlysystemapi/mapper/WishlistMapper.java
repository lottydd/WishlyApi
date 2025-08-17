package com.lotty.wishlysystemapi.mapper;


import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.model.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface WishlistMapper {

    @Mapping(target = "wishlistId", ignore = true) // генерируется БД
    @Mapping(target = "user", ignore = true)       // установим вручную
    @Mapping(target = "itemList", ignore = true)   // пустой при создании
    @Mapping(target = "itemCount", constant = "0")
    @Mapping(target = "createDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "modifiedDate", expression = "java(java.time.LocalDateTime.now())")
    Wishlist toEntity (WishlistCreateDTO dto);

    Wishlist toDTO(Wishlist wishlist);
}
