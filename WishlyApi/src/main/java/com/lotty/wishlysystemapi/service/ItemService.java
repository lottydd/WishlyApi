package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.ItemDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ItemMapper itemMapper;
    private final ItemDAO itemDAO;
    private final UserDAO userDAO;

    public ItemService(ItemMapper itemMapper, ItemDAO itemDAO, UserDAO userDAO) {
        this.itemMapper = itemMapper;
        this.itemDAO = itemDAO;
        this.userDAO = userDAO;
    }

    @Transactional
    public Item createItem(AddItemToWishlistDTO dto) {
        logger.info("Creating new item for user: {}", dto.getUserId());
        User owner = userDAO.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Item item = itemMapper.toEntity(dto);
        item.setOwner(owner);
        return itemDAO.save(item);
    }

    @Transactional(readOnly = true)
    public Item getItemById(Integer itemId) {
        return itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
    }

    @Transactional
    public Item updateItem(UpdateItemDTO dto) {
        Item item = itemDAO.findById(dto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setItemName(dto.getItemName());
        item.setSourceURL(dto.getSourceURL());
        item.setImageURL(dto.getImageURL());

        return itemDAO.save(item);
    }

    @Transactional
    public void deleteItem(Integer itemId) {
        Item item = itemDAO.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        logger.info("Deleting item with ID: {}", itemId);
        itemDAO.delete(item);
    }

    @Transactional(readOnly = true)
    public List<Item> getUserItems(Integer userId) {
        return itemDAO.findAllByOwnerId(userId);
    }
}
