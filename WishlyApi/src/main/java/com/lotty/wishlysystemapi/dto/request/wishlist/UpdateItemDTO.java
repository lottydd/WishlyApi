package com.lotty.wishlysystemapi.dto.request.wishlist;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateItemDTO {

    private String description;
    private Integer itemId;
    private String itemName;
    private String sourceURL;
    private Double price;
}
