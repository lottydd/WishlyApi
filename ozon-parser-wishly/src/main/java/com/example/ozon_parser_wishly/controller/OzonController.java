package com.example.ozon_parser_wishly.controller;

import com.example.ozon_parser_wishly.service.OzonParserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ozon")
public class OzonController {

    private final OzonParserService parserService;

    public OzonController(OzonParserService parserService) {
        this.parserService = parserService;
    }

    @GetMapping("/parse")
    public Map<String, String> parse(@RequestParam String url) {
        return parserService.parseProduct(url);
    }
}