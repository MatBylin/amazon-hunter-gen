package amz.constants;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;

@UtilityClass
public class AmazonLocators {
    public static final By PRODUCT_PRICE = By.cssSelector("#corePriceDisplay_desktop_feature_div .a-price-whole");
}
