package com.lotty.wishlysystemapi.repository;

import com.lotty.wishlysystemapi.model.Wishlist;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WishlistDAO extends BaseDAO<Wishlist, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(WishlistDAO.class);

    public WishlistDAO() {
        super(Wishlist.class);
    }

    public List<Wishlist> findAllByUserId(Integer userId) {
        logger.info("Поиск всех вишлистов пользователя с ID {}", userId);
        return entityManager.createQuery(
                        "SELECT w FROM Wishlist w WHERE w.user.userId = :userId", Wishlist.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Wishlist> findAllByUserIdWithItems(Integer userId) {
        logger.info("Поиск всех вишлистов пользователя с ID {} с загрузкой items", userId);
        return entityManager.createQuery(
                        "SELECT DISTINCT w FROM Wishlist w " +
                                "LEFT JOIN FETCH w.wishlistItems " +
                                "WHERE w.user.userId = :userId", Wishlist.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<Wishlist> findByIdWithItems(Integer wishlistId) {
        logger.info("Поиск вишлиста с ID {} с загрузкой items", wishlistId);
        try {
            Wishlist wishlist = entityManager.createQuery(
                            "SELECT DISTINCT w FROM Wishlist w " +
                                    "LEFT JOIN FETCH w.wishlistItems " +
                                    "WHERE w.wishlistId = :wishlistId", Wishlist.class)
                    .setParameter("wishlistId", wishlistId)
                    .getSingleResult();
            return Optional.of(wishlist);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }



    public boolean existsById(Integer wishlistId) {
        logger.info("Проверка существования вишлиста с ID {}", wishlistId);
        Integer count = entityManager.createQuery(
                        "SELECT COUNT(w) FROM Wishlist w WHERE w.wishlistId = :id", Integer.class)
                .setParameter("id", wishlistId)
                .getSingleResult();
        return count > 0;
    }

    public List<Wishlist> findAllByItemId(Integer itemId) {
        logger.info("Поиск всех вишлистов, содержащих айтем с ID {}", itemId);
        return entityManager.createQuery(
                        "SELECT w FROM Wishlist w JOIN w.wishlistItems i WHERE i.itemId = :itemId", Wishlist.class)
                .setParameter("itemId", itemId)
                .getResultList();
    }
}