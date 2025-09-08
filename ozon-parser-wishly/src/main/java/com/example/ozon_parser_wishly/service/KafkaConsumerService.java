package com.example.ozon_parser_wishly.service;

import com.example.common.dto.ItemParseResponseDTO;
import com.example.common.dto.ParseRequestDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {
    private final OzonParserService ozonParserService;
    private final WildberriesParserService wbParserService;
    private final KafkaTemplate<String, ItemParseResponseDTO> kafkaTemplate;

    public KafkaConsumerService(OzonParserService ozonParserService,
                                WildberriesParserService wbParserService,
                                KafkaTemplate<String, ItemParseResponseDTO> kafkaTemplate) {
        this.ozonParserService = ozonParserService;
        this.wbParserService = wbParserService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "parse-requests", groupId = "parser-group")
    public void consume(ParseRequestDTO request) {
        try {
            // Парсим как обычно
            ItemParseResponseDTO response = parseProduct(request.getUrl());

            // Добавляем taskId в ответ
            response.setTaskId(request.getTaskId());

            kafkaTemplate.send("parse-results", response);
        } catch (Exception e) {
            // Отправляем ответ с ошибкой
            ItemParseResponseDTO errorResponse = new ItemParseResponseDTO();
            errorResponse.setTaskId(request.getTaskId());
            errorResponse.setErrorMessage("Ошибка парсинга: " + e.getMessage());
            kafkaTemplate.send("parse-results", errorResponse);
        }
    }


    private ItemParseResponseDTO parseProduct(String url) {
        if (url.contains("ozon.ru") || url.contains("ozon.")) {
            return ozonParserService.parseProduct(url);

        } else if (url.contains("wildberries.ru") || url.contains("wb.ru")) {
            return wbParserService.parseProduct(url);

        } else {
            throw new UnsupportedOperationException("Сайт не поддерживается: " + url);
        }
    }
}