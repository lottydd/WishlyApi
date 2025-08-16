package com.example.ozon_parser_wishly.controller;

import com.example.ozon_parser_wishly.service.OzonParserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/ozon")
public class OzonParserController {

    private final OzonParserService parserService;

    public OzonParserController(OzonParserService parserService) {
        this.parserService = parserService;
    }

    @GetMapping("/parse")
    public Map<String, Object> parse(@RequestParam String url) {
        return parserService.parseProduct(url);
    }
}