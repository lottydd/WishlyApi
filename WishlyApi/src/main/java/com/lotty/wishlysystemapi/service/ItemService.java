package com.lotty.wishlysystemapi.service;

import com.example.common.dto.ItemParseResponseDTO;
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
        logger.info("Создание нового айтема для пользователя ID: {}", dto.getUserId());
        User owner = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", dto.getUserId());
                    return new EntityNotFoundException("Пользователь не найден");
                });
        Item item = itemMapper.toEntity(dto);
        item.setOwner(owner);
        Item savedItem = itemDAO.save(item);
        logger.info("Айтем успешно создан с ID: {}", savedItem.getItemId());
        return itemMapper.toItemCreateResponseDTO(savedItem);
    }


    public Item createItemFromParsedData(ItemParseResponseDTO response) {
        logger.info("Создание item из распарсенных данных для пользователя ID: {}", response.getUserId());

        Item item = new Item();
        item.setItemName(response.getItemName());
        item.setDescription(response.getDescription());
        item.setPrice(response.getPrice());
        item.setImageURL(response.getImageURL());
        item.setSourceURL(response.getSourceURL());

        // Находим владельца
        User owner = userDAO.findById(response.getUserId())
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", response.getUserId());
                    return new EntityNotFoundException("Пользователь не найден");
                });
        item.setOwner(owner);

        Item savedItem = itemDAO.save(item);
        logger.info("Item создан из парсинга с ID: {}", savedItem.getItemId());

        return savedItem;
    }


    @Transactional
    public ItemResponseDTO updateItem(UpdateItemDTO dto) {
        logger.info("Обновление айтема с ID: {}", dto.getItemId());
        Item item = itemDAO.findById(dto.getItemId())
                .orElseThrow(() -> {
                    logger.error("Айтем с ID {} не найден для обновления", dto.getItemId());
                    return new EntityNotFoundException("Айтем не найден");
                });
        itemMapper.updateItemFromDTO(dto, item);
        Item updatedItem = itemDAO.save(item);
        logger.info("Айтем с ID {} успешно обновлён", updatedItem.getItemId());
        return itemMapper.toItemResponseDTO(updatedItem);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getUserItems(Integer userId) {
        logger.info("Получение айтемов пользователя ID: {}", userId);
        List<ItemResponseDTO> items = itemMapper.toItemResponseDTOList(itemDAO.findAllByOwnerId(userId));
        if (items.isEmpty()) {
            logger.warn("У пользователя ID {} нет айтемов", userId);
        } else {
            logger.info("Найдено {} айтемов у пользователя ID {}", items.size(), userId);
        }
        return items;
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
    public ItemResponseDTO getItemById(Integer itemId) {
        return itemMapper.toItemResponseDTO(itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Айтем не найден")));
    }

}
