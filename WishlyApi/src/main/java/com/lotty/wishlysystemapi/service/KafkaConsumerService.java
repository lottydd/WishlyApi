package com.lotty.wishlysystemapi.service;


import com.example.common.dto.ItemParseResponseDTO;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.ParsingTask;
import com.lotty.wishlysystemapi.repository.ParsingTaskDAO;
import com.lotty.wishlysystemapi.status.TaskStatus;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @KafkaListener(topics = "parse-results", groupId = "wishly-group")
    @Transactional
    public void consume(ItemParseResponseDTO response) {
        try {
            logger.info("Попытка создания айтема из распаршенных данных. taskId={}", response.getTaskId());
            Item item = itemService.createItemFromParsedData(response);
            logger.info("Айтем создан id={} — добавляем в вишлист {}", item.getItemId(), response.getWishlistId());
            wishlistService.addItemToWishlistWithoutAuthCheck(response.getWishlistId(), item.getItemId());
            parsingTaskService.markAsCompleted(response.getTaskId(), item.getItemId());
        } catch (Exception e) {
            logger.error("Ошибка в KafkaConsumerService.consume для taskId={}: {}", response.getTaskId(), e.getMessage(), e);
            try {
                parsingTaskService.updateTaskStatusToFailed(
                        response.getTaskId(),
                        TaskStatus.FAILED,
                        "Ошибка при обработке результата: " + e.getMessage()
                );
            } catch (Exception ex) {
                logger.error("Не удалось пометить task как FAILED для taskId={}: {}", response.getTaskId(), ex.getMessage(), ex);
            }
        }
    }
}