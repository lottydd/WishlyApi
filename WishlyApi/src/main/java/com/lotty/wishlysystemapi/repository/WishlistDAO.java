package com.lotty.wishlysystemapi.repository;


import com.lotty.wishlysystemapi.model.Wishlist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishlistDAO extends BaseDAO<Wishlist, Integer>{
    
    private static final Logger logger = LoggerFactory.getLogger(WishlistDAO.class);
    public WishlistDAO() {
        super(Wishlist.class);
    }

    public List<Wishlist> findAllByUserID(Integer id) {
    }
}
