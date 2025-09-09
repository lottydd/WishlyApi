package com.lotty.wishlysystemapi.service;

import com.example.common.dto.ParseRequestDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, ParseRequestDTO> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);


    public KafkaProducerService(KafkaTemplate<String, ParseRequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendParseRequest(ParseRequestDTO requestDTO) {
        logger.info("Попытка отправки запроса на парс ссылки");
        kafkaTemplate.send("parse-requests", requestDTO);
    }
}