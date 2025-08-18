package com.lotty.wishlysystemapi.repository;

import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.ItemList;

public class ItemListDAO extends BaseDAO<ItemList, Integer>{

    public ItemListDAO() {
        super(ItemList.class);
    }


    public void deleteByItemAndUserId(Integer userId, Integer itemId) {
    }

    public Item findItemByUserAndItemId() {
    }
}
