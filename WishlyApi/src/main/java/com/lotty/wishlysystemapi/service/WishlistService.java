package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistUpdateDTO;
import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.*;
import com.lotty.wishlysystemapi.exception.SecurityAuthenticationException;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.mapper.WishlistMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ItemDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import com.lotty.wishlysystemapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public WishlistService(WishlistMapper wishlistMapper, UserDAO userDAO,
                           WishlistDAO wishlistDAO, ItemDAO itemDAO, ItemMapper itemMapper, ItemService itemService, UserService userService) {
        this.wishlistMapper = wishlistMapper;
        this.userDAO = userDAO;
        this.wishlistDAO = wishlistDAO;
        this.itemDAO = itemDAO;
        this.itemMapper = itemMapper;
        this.itemService = itemService;
        this.userService = userService;
    }


    @Transactional
    public WishlistCreateResponseDTO createWishlist(WishlistCreateDTO dto) {
        String username = SecurityUtils.getCurrentUsername();
        logger.info("Создание вишлиста для пользователя {}", username);

        User user = userService.findUserByNameOrThrow(username);

        Wishlist wishlist = wishlistMapper.toEntity(dto);
        wishlist.setUser(user);
        wishlist.setCreateDate(LocalDateTime.now());
        wishlist.setModifiedDate(LocalDateTime.now());
        Wishlist savedWishlist = wishlistDAO.save(wishlist);

        logger.info("Вишлист успешно создан с ID: {} для пользователя {}", savedWishlist.getWishlistId(), username);
        return wishlistMapper.toWishlistCreateDTO(savedWishlist);
    }


    @Transactional
    public WishlistUpdateResponseDTO addItemToWishlist(Integer wishlistId, Integer itemId) {
        logger.info("Добавление айтема {} в вишлист {}", itemId, wishlistId);

        Wishlist wishlist = findWishlistByIdOrThrow(wishlistId);
        checkWishlistAccess(wishlist);

        Item item = itemService.findItemByIdOrThrow(itemId);

        if (wishlist.getWishlistItems().contains(item)) {
            logger.warn("Попытка добавить айтем с ID {}, который уже существует в вишлисте {}", item.getItemId(), wishlistId);
            throw new IllegalArgumentException(
                    String.format("Айтем с ID %d уже содержится в вишлисте %d", item.getItemId(), wishlistId)
            );
        }

        validateItemOwnership(item, wishlist.getUser());
        wishlist.getWishlistItems().add(item);
        item.getInWishlists().add(wishlist);
        wishlist.setModifiedDate(LocalDateTime.now());

        Wishlist updatedWishlist = wishlistDAO.save(wishlist);
        logger.info("Айтем {} добавлен в вишлист {}", itemId, wishlistId);

        return wishlistMapper.toWishlistUpdateDTO(updatedWishlist);
    }

    //controller
    @Transactional
    public WishlistUpdateResponseDTO createAndAddItemToWishlist(Integer wishlistId, AddItemToWishlistDTO dto) {
        logger.info("Создание и добавление айтема в вишлист {}", wishlistId);

        Wishlist wishlist = findWishlistByIdOrThrow(wishlistId);
        checkWishlistAccess(wishlist); // Проверка прав на вишлист

        ItemCreateResponseDTO itemResponse = itemService.createItem(dto, wishlist.getUser().getUsername());
        return addItemToWishlist(wishlistId, itemResponse.getItemId());
    }

    @Transactional(readOnly = true)
    public List<WishlistResponseDTO> getUserWishlists(String username) {
        logger.info("Получение вишлистов пользователя: {}", username);

        User targetUser = userService.findUserByNameOrThrow(username);
        checkUserWishlistsAccess(targetUser);
        List<Wishlist> wishlists = wishlistDAO.findAllByUserIdWithItems(targetUser.getUserId());

        if (wishlists.isEmpty()) {
            logger.warn("У пользователя ID {} нет вишлистов", username);
        } else {
            logger.info("Найдено {} вишлистов у пользователя ID {}", wishlists.size(), username);
        }
        return wishlistMapper.toWishlistResponseDTOList(wishlists);
    }

    @Transactional
    public WishlistUpdateResponseDTO updateWishlist(WishlistUpdateDTO dto) {
        logger.info("Обновление вишлиста ID: {}", dto.getWishlistId());

        Wishlist wishlist = findByIdWithItemsOrThrow(dto.getWishlistId());
        checkWishlistAccess(wishlist);

        wishlist.setWishlistName(dto.getWishlistName());
        wishlist.setWishlistDescription(dto.getWishlistDescription());
        wishlist.setModifiedDate(LocalDateTime.now());

        Wishlist updatedWishlist = wishlistDAO.save(wishlist);
        logger.info("Вишлист ID {} успешно обновлен", dto.getWishlistId());

        return wishlistMapper.toWishlistUpdateDTO(updatedWishlist);
    }

    @Transactional
    public void deleteWishlist(Integer wishlistId) {
        logger.info("Удаление вишлиста ID: {}", wishlistId);

        Wishlist wishlist = findWishlistByIdOrThrow(wishlistId);
        checkWishlistAccess(wishlist);

        wishlistDAO.delete(wishlistId);
        logger.info("Вишлист ID {} успешно удален", wishlistId);
    }

    @Transactional
    public WishlistUpdateResponseDTO removeItemFromWishlist(Integer wishlistId, Integer itemId) {
        logger.info("Удаление айтема {} из вишлиста {}", itemId, wishlistId);

        Wishlist wishlist = findByIdWithItemsOrThrow(wishlistId);
        checkWishlistAccess(wishlist);

        Item item = itemService.findItemByIdOrThrow(itemId);
        validateItemInWishlist(wishlist, item);
        wishlist.getWishlistItems().remove(item);
        wishlist.setModifiedDate(LocalDateTime.now());
        Wishlist updatedWishlist = wishlistDAO.save(wishlist);

        logger.info("Айтем {} удален из вишлиста {}", itemId, wishlistId);
        return wishlistMapper.toWishlistUpdateDTO(updatedWishlist);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getWishlistItems(Integer wishlistId) {
        logger.info("Получение айтемов для вишлиста ID: {}", wishlistId);

        Wishlist wishlist = findByIdWithItemsOrThrow(wishlistId);
        checkWishlistAccess(wishlist);
        List<ItemResponseDTO> items = itemMapper.toItemResponseDTOList(wishlist.getWishlistItems());

        if (items.isEmpty()) {
            logger.warn("Вишлист ID {} пуст", wishlistId);
        } else {
            logger.info("Вишлист ID {} содержит {} айтемов", wishlistId, items.size());
        }
        return items;
    }

    @Transactional(readOnly = true)
    public WishlistPrivateInfoDTO getWishlistPrivateInfoById(Integer wishlistId) {
        logger.info("Получение вишлиста ID: {}", wishlistId);
        Wishlist wishlist = findByIdWithItemsOrThrow(wishlistId);
        checkWishlistAccess(wishlist);
        return wishlistMapper.toWishlistPrivateInfoDTO(wishlist);
    }

    @Transactional(readOnly = true)
    public WishlistResponseDTO getWishlistInfo(Integer wishlistId) {
        logger.info("Получение вишлиста ID: {}", wishlistId);
        Wishlist wishlist = findWishlistByIdOrThrow(wishlistId);
        return wishlistMapper.toWishlistDTO(wishlist);
    }

    @Transactional(readOnly = true)
    public WishlistWithItemsResponseDTO getWishlistInfoWithItemList(Integer wishlistId) {
        logger.info("Получение вишлиста ID: {}", wishlistId);
        Wishlist wishlist = findByIdWithItemsOrThrow(wishlistId);
        return wishlistMapper.toWishlistWithItemsDTO(wishlist);
    }

    private Wishlist findByIdWithItemsOrThrow(Integer wishlistId) {
        return wishlistDAO.findByIdWithItems(wishlistId)
                .orElseThrow(() -> {
                    logger.error("Вишлист с ID {} не найден", wishlistId);
                    return new EntityNotFoundException("Вишлист не найден");
                });
    }


    private Wishlist findWishlistByIdOrThrow(Integer wishlistId) {
        return wishlistDAO.findById(wishlistId)
                .orElseThrow(() -> {
                    logger.error("Вишлист с ID {} не найден", wishlistId);
                    return new EntityNotFoundException("Вишлист не найден");
                });
    }

    private void checkWishlistAccess(Wishlist wishlist) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!wishlist.getUser().getUsername().equals(currentUsername) && !SecurityUtils.isAdmin()) {
            logger.error("Попытка несанкционированного доступа к вишлисту {} пользователем {}",
                    wishlist.getWishlistId(), currentUsername);
            throw new SecurityAuthenticationException("Недостаточно прав для изменения вишлиста");
        }
    }

    private void checkUserWishlistsAccess(User targetUser) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!targetUser.getUsername().equals(currentUsername) && !SecurityUtils.isAdmin()) {
            logger.error("Попытка несанкционированного доступа к вишлистам пользователя {} пользователем {}",
                    targetUser.getUsername(), currentUsername);
            throw new SecurityAuthenticationException("Недостаточно прав для просмотра вишлистов");
        }
    }

    private void validateItemOwnership(Item item, User wishlistOwner) {
        if (!item.getOwner().getUserId().equals(wishlistOwner.getUserId())) {
            logger.error("Айтем {} не принадлежит владельцу вишлиста {}",
                    item.getItemId(), wishlistOwner.getUserId());
            throw new SecurityAuthenticationException("Айтем не принадлежит владельцу вишлиста");
        }
    }

    private void validateItemNotInWishlist(Wishlist wishlist, Item item) {
        if (wishlist.getWishlistItems().contains(item)) {
            logger.warn("Айтем {} уже находится в вишлисте {}", item.getItemId(), wishlist.getWishlistId());
            throw new IllegalArgumentException("Айтем уже находится в вишлисте");
        }
    }

    private void validateItemInWishlist(Wishlist wishlist, Item item) {
        if (!wishlist.getWishlistItems().contains(item)) {
            logger.warn("Айтем {} не найден в вишлисте {}", item.getItemId(), wishlist.getWishlistId());
            throw new IllegalArgumentException("Айтем не найден в вишлисте");
        }
    }
}