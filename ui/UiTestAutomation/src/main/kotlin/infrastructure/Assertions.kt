package infrastructure

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.openqa.selenium.WebElement

fun WebElement.shouldHaveText(expected: String) {
    assertThat(this.text.trim(), equalTo(expected))
}

fun WebElement.getSelector(): String {
    return this.toString().substringAfter("-> ")
}
