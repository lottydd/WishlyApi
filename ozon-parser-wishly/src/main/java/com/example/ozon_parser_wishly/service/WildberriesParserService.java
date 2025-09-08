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
import java.util.ArrayList;
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
        ItemParseResponseDTO itemParseResponseDTO = new ItemParseResponseDTO();

        try {
            driver.get(url);
            makeBrowserLookHuman(driver);
            humanScroll(driver);

            // Ждем появления заголовка
            wait.until((ExpectedCondition<Boolean>) d ->
                    ((JavascriptExecutor) d).executeScript("return document.querySelector('h1') !== null").equals(true)
            );

            itemParseResponseDTO.setItemName(getItemName(driver));
            itemParseResponseDTO.setImageURL(getMainImage(driver));
            itemParseResponseDTO.setSourceURL(url);
            itemParseResponseDTO.setDescription(getDescription(driver));
            itemParseResponseDTO.setPrice(getPrice(driver));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return itemParseResponseDTO;
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
            int scrollAmount = 200 + (int) (Math.random() * 300);
            js.executeScript("window.scrollBy(0, arguments[0]);", scrollAmount);
            try {
                Thread.sleep(300 + (int) (Math.random() * 700));
            } catch (InterruptedException ignored) {
            }
        }
        js.executeScript("window.scrollTo(0, 0);");

    }


    private String getItemName(WebDriver driver) {
        try {
            WebElement nameEl = driver.findElement(By.cssSelector("h1.productTitle--J2W7I"));
            return nameEl.getText().trim();
        } catch (NoSuchElementException e) {
            return "Название не найдено";
        }
    }

    // --- описание ---
    private String getDescription(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            // жмём на кнопку "Характеристики и описание"
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.btnDetail--im7UR")));
            btn.click();

            // ждём появления блока с описанием
            WebElement desc = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("p.descriptionText--Jq9n2")));

            return desc.getText().trim();
        } catch (TimeoutException | NoSuchElementException e) {
            return "Описание не найдено";
        }
    }

    // --- цена ---
    private Double getPrice(WebDriver driver) {
        try {
            // Сначала ищем "кошелек" — акционная цена
            List<WebElement> walletPriceElems = driver.findElements(By.cssSelector("span.priceBlockWalletPrice--RJGuT"));
            for (WebElement el : walletPriceElems) {
                String priceText = el.getText().replaceAll("[^0-9]", "");
                if (!priceText.isEmpty()) {
                    logger.info("Wallet price digits only: '{}'", priceText);
                    return Double.valueOf(priceText);
                }
            }

            // Если цены кошелька нет, берём финальную цену
            List<WebElement> finalPriceElems = driver.findElements(By.cssSelector("ins.priceBlockFinalPrice--iToZR"));
            for (WebElement el : finalPriceElems) {
                String priceText = el.getText().replaceAll("[^0-9]", "");
                if (!priceText.isEmpty()) {
                    logger.info("Final price digits only: '{}'", priceText);
                    return Double.valueOf(priceText);
                }
            }
        } catch (Exception e) {
            logger.error("Error while parsing price", e);
        }
        return null;
    }








    // --- картинка ---
    private String getMainImage(WebDriver driver) {
        try {
            WebElement img = driver.findElement(By.cssSelector("div.imageContainer--TnaxW img"));
            String src = img.getAttribute("src");
            return (src != null && !src.isEmpty()) ? src : null;
        } catch (NoSuchElementException e) {
            return null;
        }

    }
}