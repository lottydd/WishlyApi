package com.example.ozon_parser_wishly.controller;

import com.example.common.dto.ItemParseResponseDTO;
import com.example.common.dto.ParseRequestDTO;
import com.example.ozon_parser_wishly.service.OzonParserService;
import com.example.ozon_parser_wishly.service.WildberriesParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parse")
public class ParserController {

    private final OzonParserService ozonParserService;
    private final WildberriesParserService wbParserService;

    public ParserController(OzonParserService ozonParserService,
                            WildberriesParserService wbParserService) {
        this.ozonParserService = ozonParserService;
        this.wbParserService = wbParserService;
    }

    @GetMapping("/parse")
    public ResponseEntity<ItemParseResponseDTO> parseProduct(@RequestParam ParseRequestDTO parseRequestDTO) {
        ItemParseResponseDTO response;
        if (parseRequestDTO.getUrl().contains("ozon.ru")) {
            response = ozonParserService.parseProduct(parseRequestDTO.getUrl());
            return ResponseEntity.ok(response);

        } else if (parseRequestDTO.getUrl().contains("wildberries.ru")) {
            response = wbParserService.parseProduct(parseRequestDTO.getUrl());
            return ResponseEntity.ok(response);

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ItemParseResponseDTO());
        }
    }
}
