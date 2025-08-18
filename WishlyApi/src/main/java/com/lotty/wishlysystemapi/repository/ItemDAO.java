package com.lotty.wishlysystemapi.repository;


import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.Wishlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemDAO  extends BaseDAO<Item, Integer>{

    private static final Logger logger = LoggerFactory.getLogger(ItemDAO.class);

    public ItemDAO( ) {
        super(Item.class);
    }

    public List<Item> findAllByOwnerId(Integer userId) {
    }
}
