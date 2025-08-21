package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ItemDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ItemMapper itemMapper;
    private final ItemDAO itemDAO;
    private final UserDAO userDAO;
    private final WishlistDAO wishlistDAO;

    @Autowired
    public ItemService(ItemMapper itemMapper, ItemDAO itemDAO, UserDAO userDAO, WishlistDAO wishlistDAO) {
        this.itemMapper = itemMapper;
        this.itemDAO = itemDAO;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
    }

    @Transactional
    public ItemCreateResponseDTO createItem(AddItemToWishlistDTO dto) {
        logger.info("Создание нового айтема для Пользователя: {}", dto.getUserId());
        User owner = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        Item item = itemMapper.toEntity(dto);
        item.setOwner(owner);
        return itemMapper.toItemCreateResponseDTO(itemDAO.save(item));
    }

    @Transactional
    public ItemResponseDTO updateItem(UpdateItemDTO dto) {
        Item item = itemDAO.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Айтем не найден"));
        itemMapper.updateItemFromDTO(dto, item);
        Item updatedItem = itemDAO.save(item);
        return itemMapper.toItemResponseDTO(updatedItem);
    }

    @Transactional
    public void deleteItem(Integer itemId) {
        Item item = itemDAO.findById(itemId)
                .orElseThrow(() -> {
                    logger.error("Айтем с ID {} не найден", itemId);
                    return new EntityNotFoundException("Айтем не найден");
                });

        logger.info("Начат процесс удаления айтема с ID: {}", itemId);

        //(ManyToMany)
        List<Wishlist> wishlistsWithItem = wishlistDAO.findAllByItemId(itemId);
        for (Wishlist wishlist : wishlistsWithItem) {
            wishlist.getWishlistItems().remove(item);
            wishlistDAO.save(wishlist);
            logger.debug("Айтем удален из вишлиста ID: {}", wishlist.getWishlistId());
        }

        itemDAO.delete(item.getItemId());
        logger.info("Айтем с ID {} полностью удален", itemId);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getUserItems(Integer userId) {
        return itemMapper.toItemResponseDTOList(itemDAO.findAllByOwnerId(userId));
    }

    @Transactional(readOnly = true)
    public ItemResponseDTO getItemById(Integer itemId) {
        return itemMapper.toItemResponseDTO(itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Айтем не найден")));
    }
}
