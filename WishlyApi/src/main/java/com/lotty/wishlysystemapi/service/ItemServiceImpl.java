package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.entity.ItemEntity;
import com.lotty.wishlysystemapi.model.Item;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item createItem(Item item) {
        ItemEntity itemEntity = new ItemEntity();
        BeanUtils.copyProperties(item, itemEntity);
        itemEntity.setQuantity(1); // Установим начальное количество айтемов
        ItemEntity savedItemEntity = itemRepository.save(itemEntity);
        Item savedItem = new Item();
        BeanUtils.copyProperties(savedItemEntity, savedItem);
        return savedItem;
    }

    @Override
    public Item updateItem(Long id, Item item) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));

        BeanUtils.copyProperties(item, itemEntity);
        ItemEntity updatedItemEntity = itemRepository.save(itemEntity);
        Item updatedItem = new Item();
        BeanUtils.copyProperties(updatedItemEntity, updatedItem);
        return updatedItem;
    }

    @Override
    public boolean deleteItem(Long id) {
        itemRepository.deleteById(id);
        return true;
    }

    @Override
    public Item getItemById(Long id) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        Item item = new Item();
        BeanUtils.copyProperties(itemEntity, item);
        return item;
    }

    @Override
    public List<Item> getAllItems() {
        List<ItemEntity> itemEntities = itemRepository.findAll();
        return itemEntities.stream()
                .map(itemEntity -> {
                    Item item = new Item();
                    BeanUtils.copyProperties(itemEntity, item);
                    return item;
                })
                .collect(Collectors.toList());
    }
}
