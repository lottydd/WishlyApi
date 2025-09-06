package com.lotty.wishlysystemapi.service;


import com.example.common.dto.ItemParseResponseDTO;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.status.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final ItemService itemService;
    private final WishlistService wishlistService;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ParsingTaskService parsingTaskService;


    public KafkaConsumerService(ItemService itemService, WishlistService wishlistService, ParsingTaskService parsingTaskService) {
        this.itemService = itemService;
        this.wishlistService = wishlistService;
        this.parsingTaskService = parsingTaskService;
    }
    @KafkaListener(topics = "parse-results")
    public void consume(ItemParseResponseDTO response) {
        try {
            if (response.getErrorMessage() != null) {
                // Обновляем статус задачи на FAILED
                parsingTaskService.updateTaskStatus(
                        response.getTaskId(),
                        TaskStatus.FAILED,
                        response.getErrorMessage()
                );
            } else {
                // Создаем товар
                Item item = itemService.createItemFromParsedData(response);

                wishlistService.addExistingItemToWishlist( response.getWishlistId(), item);

                // Обновляем статус задачи на COMPLETED
                parsingTaskService.markAsCompleted(response.getTaskId(), item.getItemId());
            }
        } catch (Exception e) {
            parsingTaskService.updateTaskStatus(
                    response.getTaskId(),
                    TaskStatus.FAILED,
                    "Ошибка при обработке результата: " + e.getMessage()
            );
        }
    }
}