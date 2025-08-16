package com.example.ozon_parser_wishly.controller;

import com.example.ozon_parser_wishly.service.WildberriesParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wildberries")
public class WildberriesParserController {

    private final WildberriesParserService parserService;

    @Autowired
    public WildberriesParserController(WildberriesParserService parserService) {
        this.parserService = parserService;
    }

    @GetMapping("/parse-wb")
    public Map<String, Object> parseWBProduct(@RequestParam String url) {
        return parserService.parseProduct(url);
    }
}
