package infrastructure

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.time.Duration


object DriverFactory {
    private val driverThreadLocal = ThreadLocal<WebDriver>()

    fun getDriver(): WebDriver {
        if (driverThreadLocal.get() == null) {
            System.setProperty("webdriver.remote.service.tracing.enabled", "false")

            val browser = (System.getProperty("browser") ?: System.getenv("browser") ?: "chrome").lowercase()
            lateinit var driver: WebDriver
            val executionMode = System.getProperty("execution_mode") ?: System.getenv("execution_mode") ?: "local"
            val isHeadless = (System.getProperty("headless") ?: System.getenv("headless") ?: "false").toBoolean()
            val isGridExecution = executionMode.equals("grid", ignoreCase = true)
            val remoteUrl = System.getProperty("grid_url") ?: System.getenv("grid_url") ?: "http://localhost:4444"

            driver = if (browser == "firefox") {
                val options = FirefoxOptions().apply {
                    if (isHeadless) {
                        addArguments("--headless")
                    }
                }
                if (isGridExecution) {
                    RemoteWebDriver(URL(remoteUrl), options)
                } else {
                    FirefoxDriver(options)
                }
            } else {
                val options = ChromeOptions().apply {
                    addArguments("--remote-allow-origins=*")
                    if (isHeadless) {
                        addArguments("--headless=new")
                    }
                }
                if (isGridExecution) {
                    RemoteWebDriver(URL(remoteUrl), options)
                } else {
                    ChromeDriver(options)
                }
            }

            driver.manage().window().maximize()
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10))
            driverThreadLocal.set(driver)
        }
        return driverThreadLocal.get()
    }

    fun quitDriver() {
        driverThreadLocal.get()?.let {
            try {
                it.quit()
            } catch (e: Exception) {
                // Ignore errors during quit, but ensure we remove the thread local
            } finally {
                driverThreadLocal.remove()
            }
        }
    }

}
