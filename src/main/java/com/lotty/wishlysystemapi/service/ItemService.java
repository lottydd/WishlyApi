package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);
    Item updateItem(Long id, Item item);
    boolean deleteItem(Long id);
    Item getItemById(Long id);
    List<Item> getAllItems();
}