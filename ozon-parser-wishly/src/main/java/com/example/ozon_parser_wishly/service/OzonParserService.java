package com.example.ozon_parser_wishly.service;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OzonParserService {

    public Map<String, String> parseProduct(String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // включаем headless
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu", "--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled"); // скрыть WebDriver
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        Map<String, String> result = new HashMap<>();

        try {
            driver.get(url);

            // Подменяем navigator.webdriver и другие свойства
            makeBrowserLookHuman(driver);

            // Скроллим страницу «по-человечески»
            humanScroll(driver);

            // Ждём появления заголовка через JS
            wait.until((ExpectedCondition<Boolean>) d ->
                    ((JavascriptExecutor) d).executeScript(
                            "return document.querySelector('div[data-widget=\"webProductHeading\"] h1') !== null"
                    ).equals(true)
            );

            // Получаем данные
            result.put("title", getText(driver, "div[data-widget='webProductHeading'] h1"));
            result.put("image", getAttr(driver, "div[data-widget='webGallery'] img", "src"));
            result.put("price", getText(driver, "span[data-widget='webPrice']"));
            result.put("description", getText(driver, "div[data-widget='webDescription']"));
            result.put("type", getText(driver, "xpath=//dt[contains(text(), 'Аромат')]/following-sibling::dd[1]"));

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
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int i = 0; i < 5; i++) {
            int scrollAmount = 200 + random.nextInt(300); // рандомная высота
            js.executeScript("window.scrollBy(0, arguments[0]);", scrollAmount);
            try {
                Thread.sleep(300 + random.nextInt(700)); // рандомная пауза
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

    private String getAttr(WebDriver driver, String selector, String attr) {
        try {
            return driver.findElement(By.cssSelector(selector)).getAttribute(attr);
        } catch (NoSuchElementException e) {
            return "Не найден";
        }
    }
}