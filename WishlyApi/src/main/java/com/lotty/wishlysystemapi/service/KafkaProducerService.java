package com.lotty.wishlysystemapi.service;

import com.example.common.dto.ParseRequestDTO;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, ParseRequestDTO> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, ParseRequestDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendParseRequest(ParseRequestDTO requestDTO) {
        kafkaTemplate.send("parse-requests", requestDTO);
    }
}