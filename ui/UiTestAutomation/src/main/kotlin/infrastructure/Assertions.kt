package infrastructure

import io.qameta.allure.Step
import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.openqa.selenium.WebElement
import java.time.Duration

@Step("Verify element {this} has exact text \"{expected}\"")
fun WebElement.shouldHaveText(expected: String) {
    assertThat(this.text.trim(), equalTo(expected))
}

fun WebElement.getSelector(): String {
    return this.toString().substringAfter("-> ")
}

fun WebElement.waitForHasNotEmptyText() {
    Awaitility.await()
        .alias("element should not have empty text")
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(100))
        .until {
            this.text.isNotEmpty()
        }
}