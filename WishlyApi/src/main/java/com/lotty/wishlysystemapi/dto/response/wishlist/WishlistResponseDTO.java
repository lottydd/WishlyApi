package com.lotty.wishlysystemapi.dto.response.wishlist;

import java.time.LocalDateTime;

public class WishlistResponseDTO {


    private Integer userId;
    private Integer wishlistId;
    private String wishlistName;
    private String wishlistDescription;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;
    private Integer itemcount;

}
