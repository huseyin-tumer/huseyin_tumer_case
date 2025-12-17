package infrastructure

import io.qameta.allure.Step
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.openqa.selenium.WebElement

@Step("Verify element {this} has exact text \"{expected}\"")
fun WebElement.shouldHaveText(expected: String) {
    assertThat(this.text.trim(), equalTo(expected))
}

fun WebElement.getSelector(): String {
    return this.toString().substringAfter("-> ")
}
