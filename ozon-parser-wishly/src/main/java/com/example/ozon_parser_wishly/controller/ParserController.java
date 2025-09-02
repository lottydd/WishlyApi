package com.example.ozon_parser_wishly.controller;

import com.example.ozon_parser_wishly.dto.response.ItemParseResponseDTO;
import com.example.ozon_parser_wishly.service.OzonParserService;
import com.example.ozon_parser_wishly.service.WildberriesParserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ParserController {

    private final OzonParserService ozonParserService;
    private final WildberriesParserService wbParserService;

    public ParserController(OzonParserService ozonParserService,
                            WildberriesParserService wbParserService) {
        this.ozonParserService = ozonParserService;
        this.wbParserService = wbParserService;
    }

    @GetMapping("/parse")
    public ItemParseResponseDTO parseProduct(@RequestParam String url) {
        if (url.contains("ozon.ru")) {
            return ozonParserService.parseProduct(url);
        } else if (url.contains("wildberries.ru")) {
            return wbParserService.parseProduct(url);
        } else {
            return new ItemParseResponseDTO("Неизвестно", "Сайт не поддерживается",
                    null, null, url);
        }
    }
}