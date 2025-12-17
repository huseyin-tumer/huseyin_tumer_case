package pages

import infrastructure.BasePage
import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class CareerPage(driver: WebDriver) : BasePage(driver) {
    private val title = By.cssSelector("h1.big-title")
    private val description = By.cssSelector("section#page-head p.text-medium")
    private val seeAllButton = By.cssSelector("section#page-head div.button-group a")

    @Step("Verify career page title is \"{text}\"")
    fun assertTitle(text: String) {
        assertText(title, text)
    }

    @Step("Verify career page description is correct")
    fun assertDescription(text: String) {
        assertText(description, text)
    }

    @Step("Verify \"See all\" button text and URL")
    fun assertSeeAllButton(text: String, url: String) {
        assertText(seeAllButton, text)
        assertHref(seeAllButton, url)
    }

    @Step("Verify career page hero section is fully loaded")
    fun assertPageLoaded() {
        assertTitle("Quality Assurance")
        assertDescription("Never miss a thing? Obsess over every little detail? Our Q&A team is committed to testing everything we build to ensure it’s flawless for our customers (and theirs).")
        assertSeeAllButton(
            text = "See all QA jobs",
            url = "https://insiderone.com/careers/open-positions/?department=qualityassurance"
        )
    }

    fun clickSeeAllButton() {
        click(seeAllButton)
    }


}