package com.lotty.wishlysystemapi.mapper;


import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistUpdateResponseDTO;
import com.lotty.wishlysystemapi.model.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")

public interface WishlistMapper {

    @Mapping(target = "wishlistId", ignore = true) // генерируется БД
    @Mapping(target = "user", ignore = true)       // установим вручную
    @Mapping(target = "itemList", ignore = true)   // пустой при создании
    @Mapping(target = "itemCount", constant = "0")
    @Mapping(target = "createDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "modifiedDate", expression = "java(java.time.LocalDateTime.now())")
    Wishlist toEntity (WishlistCreateDTO dto);

    WishlistCreateResponseDTO toWishlistCreateDTO(Wishlist wishlist);

    @Mapping(target = "itemCount", expression = "java(wishlist.getItemCount())")
    WishlistUpdateResponseDTO toWishlistUpdateDTO(Wishlist wishlist);


    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "itemCount", expression = "java(wishlist.getItemCount())")
    WishlistResponseDTO toWishlistDTO(Wishlist wishlist);

   default List<WishlistResponseDTO> toWishlistResponseDTOList(List<Wishlist> wishlists) {
        if (wishlists == null) {
            return Collections.emptyList();
        }
        return wishlists.stream()
                .map(this::toWishlistDTO)
                .toList();
    }
}
