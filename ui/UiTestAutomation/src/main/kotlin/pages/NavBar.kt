package pages

import infrastructure.BasePage
import io.qameta.allure.Step
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class NavBar(driver: WebDriver) : BasePage(driver) {
    private val platform = By.cssSelector("div.header-menu-item:not(.is-mobile)")
    private val industries = By.cssSelector("div.header-menu-item:not(.is-mobile)")
    private val customers = By.cssSelector("div.header-menu-item:not(.is-mobile)")
    private val resources = By.cssSelector("div.header-menu-item:not(.is-mobile)")

    @Step("Verify main navigation bar items are visible")
    fun assertNavBarLoaded() {
        shouldBeVisible(platform, "Platform is not visible")
        shouldBeVisible(industries, "Industries is not visible")
        shouldBeVisible(customers, "Customers is not visible")
        shouldBeVisible(resources, "Resources is not visible")
    }
}