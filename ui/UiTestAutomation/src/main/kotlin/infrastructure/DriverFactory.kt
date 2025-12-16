package infrastructure

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URL
import java.time.Duration


object DriverFactory {
    private val driverThreadLocal = ThreadLocal<WebDriver>()

    fun getDriver(): WebDriver {
        if (driverThreadLocal.get() == null) {
            System.setProperty("webdriver.remote.service.tracing.enabled", "false")

            val options = ChromeOptions()
            options.addArguments("--remote-allow-origins=*")
            //options.addArguments("--disable-dev-shm-usage")
            //options.addArguments("--no-sandbox")
            //options.addArguments("--disable-gpu")
            //options.addArguments("--disable-extensions")
            //options.addArguments("--headless=new")

            val executionMode = System.getProperty("execution_mode", "local")
            val driver: WebDriver
            if (executionMode.equals("grid", ignoreCase = true)) {
                val remoteUrl = "http://localhost:4444"
                driver = RemoteWebDriver(URL(remoteUrl), options)
            } else {
                driver = ChromeDriver(options)
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
