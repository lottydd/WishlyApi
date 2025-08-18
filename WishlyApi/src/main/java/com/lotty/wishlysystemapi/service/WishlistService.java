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
import com.lotty.wishlysystemapi.repository.ItemListDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);


    private final ItemService itemService;
    private final WishlistMapper wishlistMapper;
    private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;
    private final ItemDAO itemDAO;
    private final ItemListService itemListService;

    public WishlistService(ItemService itemService, WishlistMapper wishlistMapper, UserDAO userDAO, WishlistDAO wishlistDAO, ItemDAO itemDAO, ItemListService itemListService) {
        this.itemService = itemService;
        this.wishlistMapper = wishlistMapper;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
        this.itemDAO = itemDAO;
        this.itemListService = itemListService;
    }

    public Wishlist createWishlist(WishlistCreateDTO dto) {
        logger.info("Попытка создания нового вишлиста");
        User user = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Wishlist wishlist = wishlistMapper.toEntity(dto);
        wishlist.setUser(user);
        return wishlistDAO.save(wishlist);
    }

    public Wishlist addItem(AddItemToWishlistDTO dto) {
        logger.info("Попытка добавления новой вещи в вишлист");
        Item item = itemService.createItem(dto);
        Wishlist wishlist = wishlistDAO.findById(dto.getWishlistId()).orElseThrow();
        wishlist.getWishlistItems().add(item);
        wishlistDAO.save(wishlist);
        return wishlistMapper.toDTO(wishlist);
    }

    public Wishlist getWishlistById(Integer id) {
        return wishlistDAO.findById(id).orElseThrow();
    }

    public List<Wishlist> getAllWishlists(Integer id) {
        logger.info("Попытка получить все вишлисты пользователя");

        return wishlistDAO.findAllByUserID(id);
    }

    public Wishlist updateWishlist(WishlistUpdateDTO wishlistUpdateDTO) {
        logger.info("Попытка обновления данных вишлиста");
        Wishlist wishlist = wishlistDAO.findById(wishlistUpdateDTO.getWishlistId()).orElseThrow();

        //нужна проверка на empty если нет то тогда выполняем
        wishlist.setDescription(wishlistUpdateDTO.getWishlistDescription());
        wishlist.setWishlistName(wishlistUpdateDTO.getWishlistName());
        wishlist.setModifiedDate(LocalDateTime.now());
        return wishlistDAO.save(wishlist);
    }

    public void deleteWishlist(Integer id) {
        logger.info("Попытка удаления вишлиста");
        wishlistDAO.delete(id);
    }

    public Wishlist removeItem(RemoveItemDTO dto) {
        logger.info("Попытка удаления вещи из вишлиста");
        Wishlist wishlist = wishlistDAO.findById(dto.getWishlistId()).orElseThrow();
        wishlist.getWishlistItems().remove(itemDAO.findById(dto.getItemId()).orElseThrow());
        itemListService.removeItemById(dto.getUserId(), dto.getItemId());
        return wishlistDAO.save(wishlist);
    }

    public List<Item> getItems(Integer id) {
        logger.info("Попытка получения всех вещей из вишлиста");
        Wishlist wishlist = wishlistDAO.findById(id).orElseThrow();
        return wishlist.getWishlistItems();
    }
}
