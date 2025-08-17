package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.model.Wishlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ItemListService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);



    public void removeItem(Integer userId, Integer itemId) {
    }

    public Wishlist addItemToWishlist(Integer userId, Integer itemId, Integer wishlistId) {



    }
}
