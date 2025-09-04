package com.example.ozon_parser_wishly.service;

import com.example.common.dto.ItemParseResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
public class OzonParserService {

    public ItemParseResponseDTO parseProduct(String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // headless режим
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu", "--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        ItemParseResponseDTO itemParseResponseDTO = new ItemParseResponseDTO();

        try {
            driver.get(url);
            makeBrowserLookHuman(driver);
            humanScroll(driver);
            wait.until((ExpectedCondition<Boolean>) d ->
                    ((JavascriptExecutor) d).executeScript(
                            "return document.querySelector('div[data-widget=\"webProductHeading\"] h1') !== null"
                    ).equals(true)
            );
            itemParseResponseDTO.setItemName(getText(driver, "div[data-widget='webProductHeading'] h1"));
            itemParseResponseDTO.setImageURL( getMainImage(driver, wait));
            itemParseResponseDTO.setSourceURL(url);
            itemParseResponseDTO.setPrice(Double.valueOf(getPrice(driver, wait)));
            itemParseResponseDTO.setDescription(getDescription(driver, wait));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
        return itemParseResponseDTO;
    }

    private String getPrice(WebDriver driver, WebDriverWait wait) {
        try {
            // 1. Ожидаем появления блока с ценой
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-widget='webPrice']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("span[class*='tsHeadline']"))
            ));

            // 2. Пробуем разные стратегии извлечения цены в порядке приоритета
            List<String> priceSelectors = Arrays.asList(
                    // Основная цена (обычная)
                    "div[data-widget='webPrice'] span.tsHeadline500Medium",
                    // Цена со скидкой (первая цена)
                    "div[data-widget='webPrice'] span.tsHeadline600Large",
                    // Альтернативные селекторы
                    "span.tsHeadline500Medium",
                    "span[class*='price']",
                    "div[class*='price']"
            );

            for (String selector : priceSelectors) {
                try {
                    WebElement priceElement = driver.findElement(By.cssSelector(selector));
                    String priceText = priceElement.getText().trim();

                    if (!priceText.isEmpty() && priceText.matches(".*[0-9].*")) {
                        // Очищаем цену от лишних символов
                        return priceText.replaceAll("[^0-9 ₽]", "").replace(" ", " ");
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }

            // 3. Если не нашли через CSS, пробуем XPath
            try {
                WebElement priceElement = driver.findElement(
                        By.xpath("//span[contains(@class, 'tsHeadline') and contains(text(), '₽')]"));
                return priceElement.getText().trim();
            } catch (NoSuchElementException e) {
                // Продолжаем
            }

            // 4. Пробуем извлечь из JSON-LD
            try {
                String jsonData = (String)((JavascriptExecutor)driver).executeScript(
                        "return document.querySelector('script[type=\"application/ld+json\"]')?.innerText"
                );

                if (jsonData != null) {
                    JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
                    if (jsonNode.has("offers") && jsonNode.get("offers").has("price")) {
                        return jsonNode.get("offers").get("price").asText() + " ₽";
                    }
                }
            } catch (Exception e) {
                // Не удалось получить данные из JSON
            }

        } catch (Exception e) {
            System.err.println("Ошибка при получении цены: " + e.getMessage());
        }
        return "Не найдена";
    }



    private void makeBrowserLookHuman(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => false});" +
                        "window.chrome = { runtime: {} };" +
                        "Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3]});" +
                        "Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']});"
        );
    }

    private void humanScroll(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 5; i++) {
            int scrollAmount = 200 + random.nextInt(300);
            js.executeScript("window.scrollBy(0, arguments[0]);", scrollAmount);
            try {
                Thread.sleep(300 + random.nextInt(700));
            } catch (InterruptedException ignored) {}
        }
        js.executeScript("window.scrollTo(0, 0);");
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
            // 1. Ожидаем загрузки главного изображения
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-widget='webGallery'] img")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[elementtiming^='lcp_eltiming_webGallery']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[src*='multimedia'][loading='eager']"))
            ));

            // 2. Пробуем найти главное изображение через разные селекторы
            List<String> selectors = Arrays.asList(
                    "img[elementtiming^='lcp_eltiming_webGallery']", // Самый приоритетный
                    "div[data-widget='webGallery'] img:first-child",
                    "img[loading='eager'][src*='multimedia']",
                    "img[fetchpriority='high'][src*='multimedia']"
            );

            for (String selector : selectors) {
                try {
                    WebElement img = driver.findElement(By.cssSelector(selector));
                    String src = img.getAttribute("src");
                    if (src == null || src.isEmpty()) {
                        src = img.getAttribute("data-src");
                    }

                    if (src != null && !src.isEmpty()) {
                        // Преобразуем URL для получения высокого разрешения
                        String highResUrl = src.replaceAll("/wc\\d+/", "/wc1000/");

                        // Проверяем, что это действительно изображение товара
                        if (highResUrl.contains("multimedia") && highResUrl.contains("/wc1000/")) {
                            return highResUrl;
                        }
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }

            // 3. Если не нашли через селекторы, пробуем извлечь из JSON-LD
            try {
                String jsonData = (String)((JavascriptExecutor)driver).executeScript(
                        "return document.querySelector('script[type=\"application/ld+json\"]')?.innerText"
                );

                if (jsonData != null) {
                    JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
                    if (jsonNode.has("image")) {
                        String mainImage = jsonNode.get("image").asText();
                        return mainImage.replaceAll("/wc\\d+/", "/wc1000/");
                    }
                }
            } catch (Exception e) {
                // Не удалось получить данные из JSON
            }

        } catch (Exception e) {
            System.err.println("Ошибка при получении главного изображения: " + e.getMessage());
        }

        // 4. Если ничего не нашли, возвращаем null или URL по умолчанию
        return null;
    }

    private List<String> getAllImages(WebDriver driver, WebDriverWait wait) {
        List<String> images = new ArrayList<>();

        try {
            // 1. Ожидаем загрузки галереи (используем несколько возможных селекторов)
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-widget='webGallery']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[class*='gallery']")),
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector("img[src*='multimedia']"))
            ));

            // 2. Прокручиваем до галереи для активации lazy-load
            ((JavascriptExecutor)driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    driver.findElement(By.cssSelector("div[data-widget='webGallery'], div[class*='gallery']"))
            );
            Thread.sleep(1000); // Даем время для подгрузки

            // 3. Собираем все возможные элементы изображений
            List<WebElement> imgElements = driver.findElements(By.cssSelector(
                    "div[data-widget='webGallery'] img, " +
                            "div[class*='gallery'] img, " +
                            "img[src*='multimedia'], " +
                            "img[elementtiming^='lcp_eltiming_webGallery']"
            ));

            // 4. Обрабатываем каждый элемент
            for (WebElement img : imgElements) {
                try {
                    String src = img.getAttribute("src");
                    if (src == null || src.isEmpty()) {
                        src = img.getAttribute("data-src"); // Проверяем data-src для lazy-load
                    }

                    if (src != null && !src.isEmpty()) {
                        // Преобразуем URL для получения максимального качества
                        String highResImage = src
                                .replace("/wc50/", "/wc1000/")
                                .replace("/wc100/", "/wc1000/")
                                .replace("/wc250/", "/wc1000/")
                                .replace("/wc500/", "/wc1000/")
                                .replace("/wc700/", "/wc1000/")
                                .replace("/thumbnail/", "/original/");

                        // Проверяем, что это изображение товара (не логотип и т.д.)
                        if (highResImage.contains("multimedia") &&
                                !highResImage.contains("logo") &&
                                !images.contains(highResImage)) {
                            images.add(highResImage);
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    continue; // Пропускаем устаревшие элементы
                }
            }

            // 5. Если изображений нет, пробуем альтернативный метод через API
            if (images.isEmpty()) {
                try {
                    String jsonData = (String)((JavascriptExecutor)driver).executeScript(
                            "return document.querySelector('script[type=\"application/ld+json\"]')?.innerText"
                    );

                    if (jsonData != null) {
                        JsonNode jsonNode = new ObjectMapper().readTree(jsonData);
                        if (jsonNode.has("image")) {
                            String mainImage = jsonNode.get("image").asText();
                            images.add(mainImage.replace("/wc50/", "/wc1000/"));
                        }
                    }
                } catch (Exception e) {
                    // Не удалось получить данные из JSON
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при получении изображений: " + e.getMessage());
        }

        // 6. Удаляем дубликаты и возвращаем результат
        return images.stream().distinct().collect(Collectors.toList());
    }

    private String getDescription(WebDriver driver, WebDriverWait wait) {
        try {
            By showMoreBtn = By.xpath("//button[contains(., 'Показать полностью')]");
            if (!driver.findElements(showMoreBtn).isEmpty()) {
                WebElement button = driver.findElement(showMoreBtn);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
                wait.until(ExpectedConditions.invisibilityOf(button));
            }

            WebElement descBlock = driver.findElement(By.cssSelector("div[data-widget='webDescription']"));
            return descBlock.getText().trim();

        } catch (Exception e) {
            return "Не найден";
        }
    }
}
