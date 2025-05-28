package amz.base;

import amz.model.Product;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
public abstract class BaseAmazon {

    protected WebDriver driver;
    protected WebDriverWait webDriverWait;
    protected String timestamp = getExecutionTimeStamp();

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver(getChromeOptions());
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        undetectedWebDriverInBrowser();
        visitAmazonBaseMainPage();
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    private ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--disk-cache-size=1");
        chromeOptions.addArguments("--media-cache-size=1");
        chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--aggressive-cache-discard");
        return chromeOptions;
    }

    private void undetectedWebDriverInBrowser() {
        ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
    }

    protected String getExecutionTimeStamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @SneakyThrows
    private void visitAmazonBaseMainPage() {
        driver.get("https://amazon.pl");
        Thread.sleep(1000);
    }

    protected void logPrice(String timestamp, String scrapedPrice, Product product) {
        log.info("Found price of: {}, for product: {}", scrapedPrice, product.getName());

        try (FileWriter writer = new FileWriter("amz.csv", true)) {
            writer.append(String.join(",", timestamp, product.getName(), scrapedPrice));
            writer.append("\n");

        } catch (IOException e) {
            throw new IllegalStateException("There was problem while creating csv file!", e);
        }
    }

    @SneakyThrows
    protected void randomWait() {
        Thread.sleep(new Random().nextInt(1000 - 500 + 1) + 500);
    }
}
