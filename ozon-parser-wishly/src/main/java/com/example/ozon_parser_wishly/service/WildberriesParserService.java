package com.example.ozon_parser_wishly.service;

import com.example.common.dto.ItemParseResponseDTO;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class WildberriesParserService {

    private static final Logger logger = LoggerFactory.getLogger(WildberriesParserService.class);

    public ItemParseResponseDTO parseProduct(String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu", "--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        ItemParseResponseDTO dto = new ItemParseResponseDTO();

        try {
            logger.info("Начало парсинга товара по URL: {}", url);
            driver.get(url);

            makeBrowserLookHuman(driver);
            humanScroll(driver);

            wait.until((ExpectedCondition<Boolean>) d ->
                    ((JavascriptExecutor) d).executeScript("return document.querySelector('h1') !== null").equals(true)
            );

            dto.setItemName(getItemName(driver));
            dto.setImageURL(getMainImage(driver));
            dto.setSourceURL(url);
            dto.setDescription(getDescription(driver));
            dto.setPrice(getPrice(driver));

            logger.info("Парсинг успешно завершен: {}", dto);
        } catch (Exception e) {
            logger.error("Ошибка при парсинге товара с URL {}", url, e);
        } finally {
            driver.quit();
            logger.debug("ChromeDriver закрыт");
        }

        return dto;
    }

    private void makeBrowserLookHuman(WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript(
                "Object.defineProperty(navigator, 'webdriver', {get: () => false});" +
                        "window.chrome = { runtime: {} };" +
                        "Object.defineProperty(navigator, 'plugins', {get: () => [1,2,3]});" +
                        "Object.defineProperty(navigator, 'languages', {get: () => ['en-US','en']});"
        );
        logger.debug("Anti-bot скрипты применены");
    }

    private void humanScroll(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 5; i++) {
            int scrollAmount = 200 + (int) (Math.random() * 300);
            js.executeScript("window.scrollBy(0, arguments[0]);", scrollAmount);
            try {
                Thread.sleep(300 + (int) (Math.random() * 700));
            } catch (InterruptedException ignored) {}
        }
        js.executeScript("window.scrollTo(0, 0);");
        logger.debug("Эмуляция скролла завершена");
    }

    private String getItemName(WebDriver driver) {
        try {
            WebElement el = driver.findElement(By.cssSelector("h1.productTitle--J2W7I"));
            return el.getText().trim();
        } catch (NoSuchElementException e) {
            logger.warn("Название не найдено");
            return "Название не найдено";
        }
    }

    private String getDescription(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.btnDetail--im7UR")));
            btn.click();
            logger.debug("Клик по кнопке 'Показать полностью' выполнен");
            WebElement desc = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("p.descriptionText--Jq9n2")));
            return desc.getText().trim();
        } catch (TimeoutException | NoSuchElementException e) {
            logger.warn("Описание не найдено");
            return "Описание не найдено";
        }
    }

    private Double getPrice(WebDriver driver) {
        try {
            List<WebElement> walletPrices = driver.findElements(By.cssSelector("span.priceBlockWalletPrice--RJGuT"));
            for (WebElement el : walletPrices) {
                String text = el.getText().replaceAll("[^0-9]", "");
                if (!text.isEmpty()) {
                    logger.info("Цена (wallet): {}", text);
                    return Double.valueOf(text);
                }
            }

            List<WebElement> finalPrices = driver.findElements(By.cssSelector("ins.priceBlockFinalPrice--iToZR"));
            for (WebElement el : finalPrices) {
                String text = el.getText().replaceAll("[^0-9]", "");
                if (!text.isEmpty()) {
                    logger.info("Цена (final): {}", text);
                    return Double.valueOf(text);
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка при парсинге цены", e);
        }
        return null;
    }

    private String getMainImage(WebDriver driver) {
        try {
            WebElement img = driver.findElement(By.cssSelector("div.imageContainer--TnaxW img"));
            String src = img.getAttribute("src");
            if (src != null && !src.isEmpty()) {
                return src;
            }
        } catch (NoSuchElementException e) {
            logger.warn("Главное изображение не найдено");
        }
        return null;
    }
}
