package utils

import infrastructure.BasePage
import org.awaitility.Awaitility
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import java.time.Duration

class WaitUtil(driver: WebDriver) : BasePage(driver) {
    fun scrollPageUntilElementAttributeValueContains(
        locator: By,
        attributeName: String,
        value: String,
        atMostTimeout: Long = 10
    ) {
        Awaitility.await()
            .atMost(Duration.ofSeconds(atMostTimeout))
            .pollInterval(Duration.ofMillis(100))
            .until {
                scrollPage(0, 200)
                getAttribute(locator, attributeName).contains(value)
            }
    }


}