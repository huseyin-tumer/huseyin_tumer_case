package infrastructure

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver

object DriverHelper {
    fun isChrome(driver: WebDriver): Boolean =
        driver is ChromeDriver || (driver is RemoteWebDriver && driver.capabilities.browserName.equals("chrome", true))

    fun isFirefox(driver: WebDriver): Boolean =
        driver is FirefoxDriver || (driver is RemoteWebDriver && driver.capabilities.browserName.equals("firefox", true))
}