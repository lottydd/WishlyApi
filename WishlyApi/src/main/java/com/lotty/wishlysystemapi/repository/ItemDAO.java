package com.lotty.wishlysystemapi.repository;

import com.lotty.wishlysystemapi.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ItemDAO extends BaseDAO<Item, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(ItemDAO.class);

    public ItemDAO() {
        super(Item.class);
    }

    public List<Item> findAllByOwnerId(Integer userId) {
        logger.info("Поиск всех айтемов пользователя с ID {}", userId);
        return entityManager.createQuery(
                        "SELECT i FROM Item i WHERE i.owner.userId = :userId", Item.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}