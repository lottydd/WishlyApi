package com.lotty.wishlysystemapi.repository;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public abstract class BaseDAO<T, ID> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;
    private static final Logger logger = LoggerFactory.getLogger(BaseDAO.class);

    @PersistenceContext
    protected EntityManager entityManager;

    protected BaseDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<T> findById(ID id) {
        logger.info("Поиск сущности {} с ID: {}", entityClass.getSimpleName(), id);
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            logger.info("Сущность {} с ID {} не найдена", entityClass.getSimpleName(), id);
        } else {
            logger.info("Сущность {} с ID {} найдена", entityClass.getSimpleName(), id);
        }
        return Optional.ofNullable(entity);
    }


    @Transactional(readOnly = true)
    @Override
    public List<T> findAll() {
        logger.info("Получение всех записей сущности {}", entityClass.getSimpleName());
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        List<T> result = entityManager.createQuery(jpql, entityClass).getResultList();
        if (result.isEmpty()) {
            logger.error("Не найдено записей сущности {}", entityClass.getSimpleName());
        } else {
            logger.info("Найдено {} записей сущности {}", result.size(), entityClass.getSimpleName());
        }
        return result;
    }

    @Transactional
    @Override
    public T save(T entity) {
        try {
            logger.info("Сохранение сущности {}", entityClass.getSimpleName());
            entityManager.persist(entity);
            logger.info("Сущность {} сохранена", entityClass.getSimpleName());
            return entity;
        } catch (Exception e) {
            logger.error("Ошибка при сохранении сущности {}", entityClass.getSimpleName(), e);
            throw e;
        }
    }
    @Transactional
    @Override
    public T update(T entity) {
        try {
            logger.info("Обновление сущности {}", entityClass.getSimpleName());
            T updatedEntity = entityManager.merge(entity);
            logger.info("Сущность {} обновлена", entityClass.getSimpleName());
            return updatedEntity;
        } catch (Exception e) {
            logger.error("Ошибка при обновлении сущности {}", entityClass.getSimpleName(), e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void delete(ID id) {
        logger.info("Удаление сущности {} с ID {}", entityClass.getSimpleName(), id);
        T entity = entityManager.find(entityClass, id);
        if (entity != null) {
            entityManager.remove(entity);
            logger.info("Сущность {} с ID {} удалена", entityClass.getSimpleName(), id);
        } else {
            logger.error("Попытка удаления несуществующей сущности {} с ID {}",
                    entityClass.getSimpleName(), id);
        }
    }

    @Transactional
    @Override
    public void saveAll(List<T> entities) {
        try {
            logger.info("Пакетное сохранение {} сущностей {}", entities.size(), entityClass.getSimpleName());
            entities.forEach(entityManager::persist);
            logger.info("Сохранено {} сущностей {}", entities.size(), entityClass.getSimpleName());
        } catch (Exception e) {
            logger.error("Ошибка при пакетном сохранении сущностей {}", entityClass.getSimpleName(), e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void flush() {
        logger.info("Синхронизация контекста с БД для сущности {}", entityClass.getSimpleName());
        entityManager.flush();
        logger.info("Синхронизация с БД выполнена");
    }


}
