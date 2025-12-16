package infrastructure

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.`is`
import org.openqa.selenium.WebElement


fun WebElement.shouldHaveText(expected: String) {
    assertThat(this.text.trim(), equalTo(expected))
}

fun WebElement.shouldContainText(expected: String) {
    assertThat(this.text.trim(), containsString(expected))
}

fun WebElement.shouldBeVisible(message: String = "Element should be visible ${this.toString()}") {
    assertThat(message, this.isDisplayed, `is`(true))
}

fun WebElement.shouldHaveAttribute(attribute: String, value: String) {
    val actualValue = this.getAttribute(attribute)
    assertThat(actualValue, equalTo(value))
}

fun WebElement.getSelector(): String {
    return this.toString().substringAfter("-> ")
}
