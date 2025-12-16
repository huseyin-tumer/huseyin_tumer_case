package infrastructure

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.WebDriver

abstract class BaseTest {
    protected lateinit var driver: WebDriver

    @BeforeEach
    fun setUp() {
        driver = DriverFactory.getDriver()
    }

    @AfterEach
    fun tearDown() {
        DriverFactory.quitDriver()
    }

    fun navigateToPath(path: String) {
        driver.navigate().to("${Environment.baseUrl}$path")
    }

}