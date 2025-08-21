package com.lotty.wishlysystemapi.dto.request.wishlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddItemToWishlistDTO {

    private Integer userId;

    private Integer wishlistId;

    private String description;

    private String sourceURL;

    private Double price;

    private String itemName;

    private String imageURL;

}
