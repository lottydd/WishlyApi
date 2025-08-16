package com.example.ozon_parser_wishly.service;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WildberriesParserService {

    public Map<String, Object> parseProduct(String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu", "--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        Map<String, Object> result = new HashMap<>();

        try {
            driver.get(url);
            makeBrowserLookHuman(driver);
            humanScroll(driver);

            // Ждем появления заголовка
            wait.until((ExpectedCondition<Boolean>) d ->
                    ((JavascriptExecutor) d).executeScript("return document.querySelector('h1') !== null").equals(true)
            );

            result.put("title", getText(driver, "h1"));
            result.put("mainImage", getMainImage(driver));
            result.put("price", getPrice(driver));
            result.put("description", getDescription(driver));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return result;
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
        for (int i = 0; i < 5; i++) {
            int scrollAmount = 200 + (int)(Math.random() * 300);
            js.executeScript("window.scrollBy(0, arguments[0]);", scrollAmount);
            try { Thread.sleep(300 + (int)(Math.random() * 700)); } catch (InterruptedException ignored) {}
        }
        js.executeScript("window.scrollTo(0, 0);");

    }



    private String getDescription(WebDriver driver) {
        try {
            // Находим кнопку "Характеристики и описание"
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement detailsBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.j-details-btn-desktop")));

            // Кликаем по кнопке, чтобы раскрыть блок
            detailsBtn.click();

            // Ждем, пока появится блок с описанием
            WebElement descSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("section.product-details__description p.option__text")));

            return descSection.getText().trim();

        } catch (TimeoutException | NoSuchElementException e) {
            return "Описание не найдено";
        }
    }


    private String getText(WebDriver driver, String selector) {
        try {
            return driver.findElement(By.cssSelector(selector)).getText();
        } catch (NoSuchElementException e) {
            return "Не найден";
        }
    }

    private List<String> getAllImages(WebDriver driver) {
        List<String> images = new ArrayList<>();
        List<WebElement> imgElements = driver.findElements(By.cssSelector("img.photo-zoom__preview.j-zoom-image"));

        for (WebElement img : imgElements) {
            String src = img.getAttribute("src");
            if (src != null && !src.isEmpty()) {
                images.add(src);
            }
        }
        return images;
    }

    private String getMainImage(WebDriver driver) {
        List<String> images = getAllImages(driver);
        return images.isEmpty() ? null : images.get(0);
    }

    private String getPrice(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Ждем, пока один из вариантов цены появится в DOM
            for (int i = 0; i < 10; i++) {
                Object finalPrice = js.executeScript(
                        "return document.querySelector('ins.price-block__final-price.wallet')?.innerText || " +
                                "document.querySelector('span.price-block__wallet-price.red-price')?.innerText || null;"
                );

                if (finalPrice != null && !finalPrice.toString().isEmpty()) {
                    String priceText = finalPrice.toString().replaceAll("[^0-9]", "");
                    if (!priceText.isEmpty()) return priceText + " ₽";
                }

                Thread.sleep(500); // ждем полсекунды и повторяем
            }

            // fallback: старая цена
            WebElement oldPrice = driver.findElement(By.cssSelector("del.price-block__old-price span"));
            String priceText = oldPrice.getText().replaceAll("[^0-9]", "");
            if (!priceText.isEmpty()) return priceText + " ₽";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (NoSuchElementException ignored) {}

        return "Не найдена";
    }

}
