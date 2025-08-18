package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.wishlist.RemoveItemDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistUpdateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
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
    private final ItemMapper itemMapper;

    public WishlistService(WishlistMapper wishlistMapper, UserDAO userDAO,
                           WishlistDAO wishlistDAO, ItemDAO itemDAO, ItemMapper itemMapper) {
        this.wishlistMapper = wishlistMapper;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
        this.itemDAO = itemDAO;
        this.itemMapper = itemMapper;
    }

    @Transactional
    public Wishlist createWishlist(WishlistCreateDTO dto) {
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Юзер не найден"));

        //Проверить маппер
        Wishlist wishlist = wishlistMapper.toEntity(dto);
        wishlist.setUser(user);
        wishlist.setCreateDate(LocalDateTime.now());
        wishlist.setModifiedDate(LocalDateTime.now());
        return wishlistDAO.save(wishlist);
    }

    @Transactional
    public Wishlist addExistingItemToWishlist(Integer wishlistId, Integer itemId) {
        Wishlist wishlist = wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Вишлист не найден"));

        Item item = itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Айтем для добавления не существует"));

        if (!wishlist.getWishlistItems().contains(item)) {
            wishlist.getWishlistItems().add(item);
            wishlist.setModifiedDate(LocalDateTime.now());
            wishlistDAO.save(wishlist);
            logger.info("Айтем с ID {} успешно добавлен в вишлист {}", itemId, wishlistId);
        } else {
            logger.warn("Попытка добавить айтем с ID {}, который уже существует в вишлисте {}", itemId, wishlistId);
            throw new IllegalArgumentException(
                    String.format("Айтем с ID %d уже содержится в вишлисте %d", itemId, wishlistId)
            );
        }
        return wishlist;
    }

    @Transactional
    public Wishlist createAndAddItemToWishlist(Integer wishlistId, AddItemToWishlistDTO dto) {
        Wishlist wishlist = wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Вишлист не найден"));
        //проверить mapper
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
                .orElseThrow(() -> new EntityNotFoundException("Вишлист не найден"));
    }

    @Transactional(readOnly = true)
    public List<Wishlist> getUserWishlists(Integer userId) {
        return wishlistDAO.findAllByUserId(userId);
    }

    @Transactional
    public Wishlist updateWishlist(WishlistUpdateDTO dto) {
        Wishlist wishlist = wishlistDAO.findById(dto.getWishlistId())
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));

        //нужен ли маппер
        wishlist.setWishlistName(dto.getWishlistName());
        wishlist.setDescription(dto.getWishlistDescription());
        wishlist.setModifiedDate(LocalDateTime.now());

        return wishlistDAO.save(wishlist);
    }

    @Transactional
    public void deleteWishlist(Integer wishlistId) {
        logger.info("Попытка удаления вишлиста с ID: {}", wishlistId);
        if (!wishlistDAO.existsById(wishlistId)) {
            logger.error("Вишлист с ID {} не найден", wishlistId);
            throw new EntityNotFoundException("Вишлист не найден");
        }
        try {
            wishlistDAO.delete(wishlistId);
            logger.info("Вишлист с ID {} успешно удален", wishlistId);
        } catch (Exception e) {
            logger.error("Ошибка при удалении вишлиста с ID {}: {}", wishlistId, e.getMessage());
            throw new RuntimeException("Не удалось удалить вишлист", e);
        }
    }

    @Transactional
    public void removeItemFromWishlist(Integer wishlistId, Integer itemId) {
        Wishlist wishlist = wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Вишлист не найден"));

        Item item = itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Айтем не найден"));

        wishlist.getWishlistItems().remove(item);
        wishlist.setModifiedDate(LocalDateTime.now());
        wishlistDAO.save(wishlist);
    }

    @Transactional(readOnly = true)
    public List<Item> getWishlistItems(Integer wishlistId) {
        return wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> new EntityNotFoundException("Вишлист не найден"))
                .getWishlistItems();
    }
}