package com.lotty.wishlysystemapi.mapper;


import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.*;
import com.lotty.wishlysystemapi.model.Wishlist;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapper.class})
public interface WishlistMapper {

    @Mapping(target = "wishlistId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "wishlistItems", ignore = true)
    @Mapping(target = "createDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "modifiedDate", expression = "java(java.time.LocalDateTime.now())")
    Wishlist toEntity(WishlistCreateDTO dto);

    @Mapping(target = "wishlistId", source = "wishlistId")
    WishlistCreateResponseDTO toWishlistCreateDTO(Wishlist wishlist);

    @Mapping(target = "wishlistId", source = "wishlistId")
    @Mapping(target = "itemCount", expression = "java(wishlist.getItemCount())")
    WishlistUpdateResponseDTO toWishlistUpdateDTO(Wishlist wishlist);

    @Mapping(target = "itemCount", expression = "java(wishlist.getItemCount())")
    WishlistResponseDTO toWishlistDTO(Wishlist wishlist);

    @Mapping(target = "wishlistItems", source = "wishlistItems")
    @Mapping(target = "itemCount", expression = "java(wishlist.getItemCount())")
    WishlistWithItemsResponseDTO toWishlistWithItemsDTO(Wishlist wishlist);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "wishlistId", source = "wishlistId")
    @Mapping(target = "itemCount", expression = "java(wishlist.getItemCount())")
    WishlistPrivateInfoDTO toWishlistPrivateInfoDTO(Wishlist wishlist);

    default List<WishlistResponseDTO> toWishlistResponseDTOList(List<Wishlist> wishlists) {
        if (wishlists == null) {
            return Collections.emptyList();
        }
        return wishlists.stream()
                .map(this::toWishlistDTO)
                .toList();
    }
}