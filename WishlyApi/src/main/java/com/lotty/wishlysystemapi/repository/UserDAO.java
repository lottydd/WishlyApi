package com.lotty.wishlysystemapi.repository;

import com.lotty.wishlysystemapi.model.User;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDAO extends BaseDAO<User, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByEmail(String email) {
        logger.info("Поиск Пользователя по email {}", email);
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        logger.info("Поиск Пользователя по username {}", username);
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        Integer count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email", Integer.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsByUsername(String username) {
        Integer count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username", Integer.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsByEmailOrUsername(String email, String username) {
        Integer count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email OR u.username = :username", Integer.class)
                .setParameter("email", email)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
}