package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.ItemList;
import com.lotty.wishlysystemapi.repository.ItemDAO;
import com.lotty.wishlysystemapi.repository.ItemListDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
@Service

public class ItemService {



    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ItemMapper itemMapper;
    private final ItemDAO itemDAO;
    private final ItemListDAO itemListDAO;

    public ItemService(ItemMapper itemMapper, ItemDAO itemDAO, ItemListDAO itemListDAO) {
        this.itemMapper = itemMapper;
        this.itemDAO = itemDAO;
        this.itemListDAO = itemListDAO;
    }

    public Item createItem(AddItemToWishlistDTO dto) {
        Item item = itemMapper.toEntity(dto);
        ItemList itemList = itemListDAO.findById(dto.getUserId()).orElseThrow();
        itemList.getItems().add(item);
        itemListDAO.save(itemList);
        return itemDAO.save(item);
    }

    public Item getItemById(RequestIdDTO dto) {
       return itemDAO.findById(dto.getId()).orElseThrow();
    }

    public Item updateItem(UpdateItemDTO dto) {
        Item item = itemDAO.findById(dto.getItemId()).orElseThrow();

        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setItemName(dto.getItemName());
        item.setSourceURL(dto.getSourceURL());

        return itemDAO.update(item);

    }

}
