package amzn;

import amzn.base.BaseAmazon;
import amzn.utils.SmsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

@Slf4j
public class AmazonTest extends BaseAmazon {

    private static final By PRICE = By.cssSelector("#corePriceDisplay_desktop_feature_div .a-price-whole");
    private static final List<List<String>> PRODUCTS = List.of(
            List.of("Orient RA-AR0004S10B", "750", "https://www.amazon.pl/Orient-Zegarek-dzie%C5%84-RA-AR0004S10B-swobodny/dp/B0DB76FTNL/ref=sr_1_13?dib=eyJ2IjoiMSJ9.0BSEhPh-yBn8u4QGC3EuoWRSN-0fpLHQoarm2T47ClnknZinEFp0Ab94B-K2HXB7c4ACZLJl9stMQneBPL1E3lhMlThmB67MQX1w4KdBYN5DPtyf9jiaNmyK0DjFyOkTARuCfOLbU_8KD0-keUBwZRJYBy-3M5iAeQQxEPGNFsUAi5Guy4akMPfxUhWjsrkB38kts9qPjTmnn93iG8xH1mIDu7-GzsYfH5PZuH7l5_lgnKlVYpqIYTD2_EjnuA-Y5KIhYawHPH61cLLRmWi1FXxDbiV4HH1YcFOwHbT64o0.OYbz1wmaMbsSp9maCP76XKc6K3_zXShKuNqTD6cgELM&dib_tag=se&qid=1747765781&refinements=p_4%3AOrient&s=fashion&sr=1-13"),
            List.of("Orient RA-AR0005Y10B", "750", "https://www.amazon.pl/Orient-Zegarek-RA-AR0005Y10B-Br%C4%85zowy-swobodny/dp/B07PTQSTSG/ref=sr_1_4?__mk_pl_PL=%C3%85M%C3%85%C5%BD%C3%95%C3%91&crid=3MLWPL5JT5UMJ&dib=eyJ2IjoiMSJ9.zTLMN5T8vXV-LDlbnucbg1inUabaR4NQaFCwadasCug4H91bxDMW6BxiZQCa3Oy80n7ZuVfMTBxfi18JBAzBtOuRVo_arCht5_ivDxss_TE.MmHpX5FruIjI9eK6lOU_Wz5BurSPeOxVorc7hAzviHo&dib_tag=se&keywords=RA-AR0004S10B&qid=1747765661&sprefix=ra-ar0004s10b%2Caps%2C104&sr=8-4"),
            List.of("DJI mini 4k", "1199", "https://www.amazon.pl/DJI-doroslych-transmisja-akumulator-inteligentny/dp/B0CXJ9GM3G/ref=sr_1_1?crid=2MDZ0ABMJZCKB&dib=eyJ2IjoiMSJ9.IVgisVHQx6S4O_csqbyFKzaMpxRs6tTPJE0W2yvFkcZ7otrL2pvmD2ilQCtmIndEewkycqVLfczS2G-g5Wd2gaumADdvlb3VvrS_bU2Xe-S4vNOPaew5hMw7B8qHFEDLrwmVjGjPWpgb1uNDskwcL8CmW2yQpCL088Qi1LrbdJD8KFANvyqc_5wY3UuAvSASwF10PYli0RGNSBaqZA791sxCSKRTHas23BTp8jTdUuIBDqmri-ITgrU3X1tbk-WlJ4odW-odSzOHmRxSFozB6rO4XOUhAwbt_L2Y5a5jKrc.jKOrS7cMUL_8W7duCp6bovwr58rRMy2fVPSgopXGv8Y&dib_tag=se&keywords=dji%2Bmini%2B4k&qid=1748282697&sprefix=dji%2Bmini%2Caps%2C125&sr=8-1&th=1")
    );

    @Test
    public void amazon() {
        ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        String timestamp = getExecutionTimeStamp();
        for (var product : PRODUCTS) {
            String amazonPrice = scrapeDataFromUrl(timestamp, product.get(0), product.get(1), product.get(2));
            log.info("Found price of: {}, for product: {}", amazonPrice, product.get(0));

            if (Integer.parseInt(amazonPrice) < Integer.parseInt(product.get(1)) && Integer.parseInt(amazonPrice) > 0) {
                log.info("Found price lower than my threshold!");
                SmsService.sendSms(amazonPrice, product.get(0));
            }
        }
    }

    @SneakyThrows
    String scrapeDataFromUrl(String timestamp, String name, String maxPrice, String url) {
        driver.get("https://amazon.pl");
        Thread.sleep(1000);
        driver.get(url);
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            webDriverWait.until(driver -> !driver.findElements(PRICE).isEmpty());
            return driver.findElement(PRICE).getText().replaceAll("\\D", "");

        } catch (TimeoutException e) {
            log.info("Price not found");
            return "0";
        }
    }
}
