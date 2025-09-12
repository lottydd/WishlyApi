package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlValidator {

    public void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new ValidationException("Ссылка не может быть пустой");
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new ValidationException("Некорректный протокол ссылки");
        }

        if (url.length() > 1000) {
            throw new ValidationException("Ссылка слишком длинная");
        }

        // Проверяем поддерживаемые домены
        if (!isSupportedDomain(url)) {
            throw new ValidationException("Неподдерживаемый сайт: " + getDomainFromUrl(url));
        }
    }

    private boolean isSupportedDomain(String url) {
        return url.contains("ozon.ru") || url.contains("ozon.") ||
                url.contains("wildberries.ru") || url.contains("wb.ru");
    }

    private String getDomainFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain != null ? domain : "неизвестный домен";
        } catch (URISyntaxException e) {
            return "некорректный URL";
        }
    }
}