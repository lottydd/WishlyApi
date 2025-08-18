package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.wishlist.RemoveItemDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistUpdateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.mapper.WishlistMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ItemDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WishlistService {
    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);
    private final WishlistMapper wishlistMapper;
    private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;
    private final ItemDAO itemDAO;

    public WishlistService(WishlistMapper wishlistMapper, UserDAO userDAO,
                           WishlistDAO wishlistDAO, ItemDAO itemDAO) {
        this.wishlistMapper = wishlistMapper;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
        this.itemDAO = itemDAO;
    }

    @Transactional
    public Wishlist createWishlist(WishlistCreateDTO dto) {
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Wishlist wishlist = wishlistMapper.toEntity(dto);
        wishlist.setUser(user);
        wishlist.setCreateDate(LocalDateTime.now());
        wishlist.setModifiedDate(LocalDateTime.now());

        return wishlistDAO.save(wishlist);
    }

    @Transactional
    public Wishlist addExistingItemToWishlist(Integer wishlistId, Integer itemId) {
        Wishlist wishlist = wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));

        Item item = itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        if (!wishlist.getWishlistItems().contains(item)) {
            wishlist.getWishlistItems().add(item);
            wishlist.setModifiedDate(LocalDateTime.now());
            wishlistDAO.save(wishlist);
        }

        return wishlist;
    }

    @Transactional
    public Wishlist createAndAddItemToWishlist(Integer wishlistId, AddItemToWishlistDTO dto) {
        Wishlist wishlist = wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));

        Item item = itemMapper.toEntity(dto);
        item.setOwner(wishlist.getUser());
        item = itemDAO.save(item);

        wishlist.getWishlistItems().add(item);
        wishlist.setModifiedDate(LocalDateTime.now());
        return wishlistDAO.save(wishlist);
    }

    @Transactional(readOnly = true)
    public Wishlist getWishlistById(Integer id) {
        return wishlistDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));
    }

    @Transactional(readOnly = true)
    public List<Wishlist> getUserWishlists(Integer userId) {
        return wishlistDAO.findAllByUserId(userId);
    }

    @Transactional
    public Wishlist updateWishlist(WishlistUpdateDTO dto) {
        Wishlist wishlist = wishlistDAO.findById(dto.getWishlistId())
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));

        wishlist.setWishlistName(dto.getWishlistName());
        wishlist.setDescription(dto.getWishlistDescription());
        wishlist.setModifiedDate(LocalDateTime.now());

        return wishlistDAO.save(wishlist);
    }

    @Transactional
    public void deleteWishlist(Integer wishlistId) {
        wishlistDAO.delete(wishlistId);
    }

    @Transactional
    public void removeItemFromWishlist(Integer wishlistId, Integer itemId) {
        Wishlist wishlist = wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));

        Item item = itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        wishlist.getWishlistItems().remove(item);
        wishlist.setModifiedDate(LocalDateTime.now());
        wishlistDAO.save(wishlist);
    }

    @Transactional(readOnly = true)
    public List<Item> getWishlistItems(Integer wishlistId) {
        return wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"))
                .getWishlistItems();
    }
}