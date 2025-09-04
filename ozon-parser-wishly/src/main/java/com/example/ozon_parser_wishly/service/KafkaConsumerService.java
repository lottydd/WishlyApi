package com.example.ozon_parser_wishly.service;

import com.example.common.dto.ItemParseResponseDTO;
import com.example.common.dto.ParseRequestDTO;
import com.example.ozon_parser_wishly.dto.response.*;
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
        ItemParseResponseDTO response;

        if (request.getUrl().contains("ozon.ru")) {
            response = ozonParserService.parseProduct(request.getUrl());
        } else if (request.getUrl().contains("wildberries.ru")) {
            response = wbParserService.parseProduct(request.getUrl());
        } else {
            response = new ItemParseResponseDTO("Неизвестно", "Сайт не поддерживается", null, null, request.getUrl());
        }

        kafkaTemplate.send("parse-results", response);
    }
}
