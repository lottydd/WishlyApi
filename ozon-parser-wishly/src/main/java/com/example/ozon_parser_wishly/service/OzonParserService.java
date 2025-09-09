package com.example.ozon_parser_wishly.service;

import com.example.common.dto.ItemParseResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OzonParserService {

    public ItemParseResponseDTO parseProduct(String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu", "--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        ItemParseResponseDTO dto = new ItemParseResponseDTO();

        try {
            log.info("Начинаем парсинг товара по URL: {}", url);
            driver.get(url);

            makeBrowserLookHuman(driver);
            humanScroll(driver);

            wait.until((ExpectedCondition<Boolean>) d ->
                    ((JavascriptExecutor) d).executeScript(
                            "return document.querySelector('div[data-widget=\"webProductHeading\"] h1') !== null"
                    ).equals(true)
            );

            dto.setItemName(getText(driver, "div[data-widget='webProductHeading'] h1"));
            dto.setImageURL(getMainImage(driver, wait));
            dto.setSourceURL(url);
            dto.setPrice(parsePrice(getPrice(driver, wait)));
            dto.setDescription(getDescription(driver, wait));

            log.info("Парсинг завершен: {}", dto);
        } catch (Exception e) {
            log.error("Ошибка при парсинге товара с URL: {}", url, e);
        } finally {
            driver.quit();
            log.debug("ChromeDriver закрыт");
        }
        return dto;
    }

    private String getPrice(WebDriver driver, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-widget='webPrice']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("span[class*='tsHeadline']"))
            ));

            List<String> priceSelectors = Arrays.asList(
                    "div[data-widget='webPrice'] span.tsHeadline500Medium",
                    "div[data-widget='webPrice'] span.tsHeadline600Large",
                    "span.tsHeadline500Medium",
                    "span[class*='price']",
                    "div[class*='price']"
            );

            for (String selector : priceSelectors) {
                try {
                    WebElement el = driver.findElement(By.cssSelector(selector));
                    String text = el.getText().trim();

                    if (!text.isEmpty() && text.matches(".*[0-9].*")) {
                        log.debug("Цена найдена по селектору {}: {}", selector, text);
                        return text.replaceAll("[^0-9 ₽]", "").replace(" ", " ");
                    }
                } catch (NoSuchElementException ignored) {
                }
            }

            try {
                WebElement el = driver.findElement(
                        By.xpath("//span[contains(@class, 'tsHeadline') and contains(text(), '₽')]"));
                return el.getText().trim();
            } catch (NoSuchElementException ignored) {

            }

            try {
                String jsonData = (String) ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelector('script[type=\"application/ld+json\"]')?.innerText"
                );
                if (jsonData != null) {
                    JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
                    if (jsonNode.has("offers") && jsonNode.get("offers").has("price")) {
                        return jsonNode.get("offers").get("price").asText() + " ₽";
                    }
                }
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            log.warn("Ошибка при получении цены: {}", e.getMessage());
        }
        return "Не найдена";
    }

    private Double parsePrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) return null;

        String cleaned = priceText.replaceAll("[^0-9.,]", "").replace(",", ".");
        try {
            return Double.valueOf(cleaned);
        } catch (NumberFormatException e) {
            log.warn("Ошибка при парсинге цены: {}", priceText);
            return null;
        }
    }

    private void makeBrowserLookHuman(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => false});" +
                        "window.chrome = { runtime: {} };" +
                        "Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3]});" +
                        "Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']});"
        );
        log.debug("Anti-bot скрипты применены");
    }

    private void humanScroll(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        for (int i = 0; i < 5; i++) {
            int amount = 200 + rnd.nextInt(300);
            js.executeScript("window.scrollBy(0, arguments[0]);", amount);
            try {
                Thread.sleep(300 + rnd.nextInt(700));
            } catch (InterruptedException ignored) {
            }
        }
        js.executeScript("window.scrollTo(0, 0);");
        log.debug("Эмуляция скролла завершена");
    }

    private String getText(WebDriver driver, String selector) {
        try {
            if (selector.startsWith("xpath=")) {
                return driver.findElement(By.xpath(selector.replace("xpath=", ""))).getText();
            } else {
                return driver.findElement(By.cssSelector(selector)).getText();
            }
        } catch (NoSuchElementException e) {
            return "Не найден";
        }
    }

    private String getMainImage(WebDriver driver, WebDriverWait wait) {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-widget='webGallery'] img")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[elementtiming^='lcp_eltiming_webGallery']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[src*='multimedia'][loading='eager']"))
            ));

            List<String> selectors = Arrays.asList(
                    "img[elementtiming^='lcp_eltiming_webGallery']",
                    "div[data-widget='webGallery'] img:first-child",
                    "img[loading='eager'][src*='multimedia']",
                    "img[fetchpriority='high'][src*='multimedia']"
            );

            for (String selector : selectors) {
                try {
                    WebElement img = driver.findElement(By.cssSelector(selector));
                    String src = Optional.ofNullable(img.getAttribute("src"))
                            .orElse(img.getAttribute("data-src"));

                    if (src != null && !src.isEmpty()) {
                        String highResUrl = src.replaceAll("/wc\\d+/", "/wc1000/");
                        if (highResUrl.contains("multimedia") && highResUrl.contains("/wc1000/")) {
                            return highResUrl;
                        }
                    }
                } catch (NoSuchElementException ignored) {
                }
            }

            try {
                String jsonData = (String) ((JavascriptExecutor) driver).executeScript(
                        "return document.querySelector('script[type=\"application/ld+json\"]')?.innerText"
                );

                if (jsonData != null) {
                    JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
                    if (jsonNode.has("image")) {
                        return jsonNode.get("image").asText().replaceAll("/wc\\d+/", "/wc1000/");
                    }
                }
            } catch (Exception ignored) {
            }

        } catch (Exception e) {
            log.warn("Ошибка при получении главного изображения: {}", e.getMessage());
        }
        return null;
    }

    private List<String> getAllImages(WebDriver driver, WebDriverWait wait) {
        List<String> images = new ArrayList<>();
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-widget='webGallery']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class*='gallery']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[src*='multimedia']"))
            ));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    driver.findElement(By.cssSelector("div[data-widget='webGallery'], div[class*='gallery']"))
            );
            Thread.sleep(1000);

            List<WebElement> elements = driver.findElements(By.cssSelector(
                    "div[data-widget='webGallery'] img, " +
                            "div[class*='gallery'] img, " +
                            "img[src*='multimedia'], " +
                            "img[elementtiming^='lcp_eltiming_webGallery']"
            ));

            for (WebElement img : elements) {
                try {
                    String src = Optional.ofNullable(img.getAttribute("src"))
                            .orElse(img.getAttribute("data-src"));

                    if (src != null && !src.isEmpty()) {
                        String highRes = src
                                .replace("/wc50/", "/wc1000/")
                                .replace("/wc100/", "/wc1000/")
                                .replace("/wc250/", "/wc1000/")
                                .replace("/wc500/", "/wc1000/")
                                .replace("/wc700/", "/wc1000/")
                                .replace("/thumbnail/", "/original/");

                        if (highRes.contains("multimedia")
                                && !highRes.contains("logo")
                                && !images.contains(highRes)) {
                            images.add(highRes);
                        }
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }

            if (images.isEmpty()) {
                try {
                    String jsonData = (String) ((JavascriptExecutor) driver).executeScript(
                            "return document.querySelector('script[type=\"application/ld+json\"]')?.innerText"
                    );

                    if (jsonData != null) {
                        JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
                        if (jsonNode.has("image")) {
                            images.add(jsonNode.get("image").asText().replace("/wc50/", "/wc1000/"));
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            log.warn("Ошибка при получении изображений: {}", e.getMessage());
        }

        return images.stream().distinct().collect(Collectors.toList());
    }

    private String getDescription(WebDriver driver, WebDriverWait wait) {
        try {
            By showMoreBtn = By.xpath("//button[contains(., 'Показать полностью')]");
            if (!driver.findElements(showMoreBtn).isEmpty()) {
                WebElement btn = driver.findElement(showMoreBtn);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                wait.until(ExpectedConditions.invisibilityOf(btn));
            }

            WebElement block = driver.findElement(By.cssSelector("div[data-widget='webDescription']"));
            return block.getText().trim();
        } catch (Exception e) {
            log.debug("Описание не найдено");
            return "Не найден";
        }
    }
}
