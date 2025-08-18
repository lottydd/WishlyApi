package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ItemListDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ItemListService {

    private static final Logger logger = LoggerFactory.getLogger(ItemListService.class);

    private final ItemListDAO itemListDAO;

    private final WishlistDAO wishlistDAO;

    public ItemListService(ItemListDAO itemListDAO, WishlistDAO wishlistDAO) {
        this.itemListDAO = itemListDAO;
        this.wishlistDAO = wishlistDAO;
    }

    public void removeItem(Integer userId, Integer itemId) {

    }

    public ItemDTO addItemToWishlist(Integer userId, Integer itemId, Integer wishlistId) {
      Item item = itemListDAO.findItemByUserAndItemId();
      Wishlist wishlist = wishlistDAO.findById(wishlistId).orElseThrow();
      wishlist.getWishlistItems().add(item);
      wishlistDAO.save(wishlist);
    }

    public void removeItemById(Integer userId, Integer itemId) {

        // itemListDAO.findItemById check if exist then delete:
        itemListDAO.deleteByItemAndUserId(userId, itemId);
    }
}
