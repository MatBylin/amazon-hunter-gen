package amz;

import amz.base.BaseAmazon;
import amz.model.Product;
import amz.model.ProductList;
import amz.utils.SmsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.TimeoutException;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static amz.constants.AmazonLocators.PRODUCT_PRICE;

@Slf4j
public class AmazonTest extends BaseAmazon {

    @Test
    public void amazon() {
        for (var product : readProducts()) {
            String amazonPrice = scrapeDataFromUrl(product);
            log.info("Found price of: {}, for product: {}", amazonPrice, product.getName());

            if (checkForPriceBelowThreshold(product, amazonPrice)) {
                log.info("Found price lower than my threshold!");
                SmsService.sendSms(amazonPrice, product.getName());
            }
        }
    }

    private static boolean checkForPriceBelowThreshold(Product product, String amazonPrice) {
        return Integer.parseInt(amazonPrice) < Integer.parseInt(product.getThreshold())
                && Integer.parseInt(amazonPrice) > 0;
    }

    @SneakyThrows
    private String scrapeDataFromUrl(Product product) {
        driver.get(product.getUrl());
        try {
            webDriverWait.until(driver -> !driver.findElements(PRODUCT_PRICE).isEmpty());
            return driver.findElement(PRODUCT_PRICE).getText().replaceAll("\\D", "");

        } catch (TimeoutException e) {
            log.info("Price not found");
            return "0";
        }
    }

    private List<Product> readProducts() {
        try (InputStream inputStream = new FileInputStream("products.yaml")) {
            ProductList productList = new Yaml().loadAs(inputStream, ProductList.class);
            return productList.getProducts();

        } catch (Exception e) {
            throw new IllegalStateException("Got exception while reading YAML file!" + e.getMessage());
        }
    }
}
