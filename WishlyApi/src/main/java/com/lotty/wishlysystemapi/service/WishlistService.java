package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.mapper.WishlistMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);

   private final WishlistMapper wishlistMapper;
   private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;


    public WishlistService(WishlistMapper wishlistMapper, UserDAO userDAO, WishlistDAO wishlistDAO) {
        this.wishlistMapper = wishlistMapper;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
    }

    public Wishlist createWishlist(WishlistCreateDTO dto) {
        logger.info("Попытка создания нового вишлиста");
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Wishlist wishlist =  wishlistMapper.toEntity(dto);
        wishlist.setUser(user);
        return wishlistDAO.save(wishlist);
    }
    
    public Wishlist addItem(Integer id, Integer itemId) {
    }


    public Wishlist getWishlistById(Integer id) {
    }

    public List<Wishlist> getAllWishlists() {
    }

    public Wishlist updateWishlist(Integer id, Wishlist wishlist) {
    }

    public void deleteWishlist(Integer id) {
    }

    public Wishlist removeItem(Integer id, Integer itemId) {
    }



    public List<Item> getItems(Integer id) {
    }

    public Wishlist updateDescription(Integer id, String description) {
    }
}
