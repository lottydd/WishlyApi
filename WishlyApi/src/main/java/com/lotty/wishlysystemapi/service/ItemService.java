package com.lotty.wishlysystemapi.service;

import com.example.common.dto.ItemParseResponseDTO;
import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.exception.SecurityAuthenticationException;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.ParsingTask;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ItemDAO;
import com.lotty.wishlysystemapi.repository.ParsingTaskDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import com.lotty.wishlysystemapi.security.SecurityUtils;
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
    private final ParsingTaskDAO parsingTaskDAO;
    private final UserService userService;

    @Autowired
    public ItemService(ItemMapper itemMapper, ItemDAO itemDAO, UserDAO userDAO,
                       WishlistDAO wishlistDAO, ParsingTaskDAO parsingTaskDAO, UserService userService) {
        this.itemMapper = itemMapper;
        this.itemDAO = itemDAO;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
        this.parsingTaskDAO = parsingTaskDAO;
        this.userService = userService;
    }

    @Transactional
    public ItemCreateResponseDTO createItem(AddItemToWishlistDTO dto, String username) {
        logger.info("Создание нового айтема для пользователя: {}", username);
        User owner = userService.findUserByNameOrThrow(username);
        checkItemAccess(owner, "создание айтема");
        Item item = itemMapper.toEntity(dto);
        item.setOwner(owner);
        Item savedItem = itemDAO.save(item);

        logger.info("Айтем успешно создан с ID: {}", savedItem.getItemId());
        return itemMapper.toItemCreateResponseDTO(savedItem);
    }

    public Item createItemFromParsedData(ItemParseResponseDTO response) {
        logger.info("Создание item из распарсенных данных. Task id: {}", response.getTaskId());

        ParsingTask task = parsingTaskDAO.findById(response.getTaskId())
                .orElseThrow(() -> {
                    logger.error("ParsingTask с ID {} не найден", response.getTaskId());
                    return new EntityNotFoundException("ParsingTask не найден");
                });

        User owner = task.getUser();
        Item item = new Item();
        item.setItemName(response.getItemName());
        item.setDescription(response.getDescription());
        item.setPrice(response.getPrice());
        item.setImageURL(response.getImageURL());
        item.setSourceURL(response.getSourceURL());
        item.setOwner(owner);

        Item savedItem = itemDAO.save(item);
        logger.info("Item создан из парсинга с ID: {} для пользователя {}", savedItem.getItemId(), owner.getUsername());
        return savedItem;
    }


    @Transactional
    public ItemResponseDTO updateItem(UpdateItemDTO dto) {
        logger.info("Обновление айтема с ID: {}", dto.getItemId());

        Item item = findItemByIdOrThrow(dto.getItemId());
        checkItemAccess(item.getOwner(), "обновления айтема");
        itemMapper.updateItemFromDTO(dto, item);
        Item updatedItem = itemDAO.save(item);

        logger.info("Айтем с ID {} успешно обновлён", updatedItem.getItemId());
        return itemMapper.toItemResponseDTO(updatedItem);
    }


    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getUserItems(String  username) {
        logger.info("Получение айтемов пользователя ID: {}", username);

        User targetUser =  userService.findUserByNameOrThrow(username);
        checkItemAccess(targetUser, "просмотра айтемов");
        List<ItemResponseDTO> items = itemMapper.toItemResponseDTOList(itemDAO.findAllByOwnerId(targetUser.getUserId()));

        if (items.isEmpty()) {
            logger.warn("У пользователя ID {} нет айтемов", username);
        } else {
            logger.info("Найдено {} айтемов у пользователя ID {}", items.size(), username);
        }
        return items;
    }

    @Transactional
    public void deleteItem(Integer itemId) {
        Item item = findItemByIdOrThrow(itemId);
        checkItemAccess(item.getOwner(), "удаления айтема");
        logger.info("Начат процесс удаления айтема с ID: {}", itemId);

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
        return itemMapper.toItemResponseDTO(findItemByIdOrThrow(itemId));
    }

    @Transactional(readOnly = true)
    public Item findItemByIdOrThrow(Integer itemId) {
        return itemDAO.findById(itemId)
                .orElseThrow(() -> {
                    logger.error("Айтем с ID {} не найден", itemId);
                    return new EntityNotFoundException("Айтем не найден");
                });
    }

    private void checkItemAccess(User itemOwner, String operation) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!itemOwner.getUsername().equals(currentUsername) && !SecurityUtils.isAdmin()) {
            logger.error("Попытка несанкционированного доступа к {} пользователем {}. Владелец: {}",
                    operation, currentUsername, itemOwner.getUsername());
            throw new SecurityAuthenticationException("Недостаточно прав для " + operation);
        }
    }
}