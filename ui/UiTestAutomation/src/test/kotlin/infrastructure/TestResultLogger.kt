package infrastructure

import io.qameta.allure.Allure
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import java.io.ByteArrayInputStream

class TestResultLogger : AfterTestExecutionCallback {
    override fun afterTestExecution(context: ExtensionContext) {
        if (context.executionException.isPresent) {
            val driver = DriverFactory.getDriver()
            if (driver is TakesScreenshot) {
                val screenshot = driver.getScreenshotAs(OutputType.BYTES)
                Allure.addAttachment("Screenshot on Failure", ByteArrayInputStream(screenshot))
            }
        }
    }
}
