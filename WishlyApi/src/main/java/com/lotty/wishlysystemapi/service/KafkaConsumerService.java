package com.lotty.wishlysystemapi.service;


import com.example.common.dto.ItemParseResponseDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final ItemService itemService;

    public KafkaConsumerService(ItemService itemService) {
        this.itemService = itemService;
    }

    @KafkaListener(topics = "parse-results", groupId = "wishly-group")
    public void consume(ItemParseResponseDTO response) {
        System.out.println("Wishly получил из Kafka: " + response.getItemName());
        // например, сохранить в базу:
        // itemService.createItemFromParsedData(response);
    }
}