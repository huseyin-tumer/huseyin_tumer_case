package pages

import infrastructure.BasePage
import models.Position
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

class OpenPositionsPage(driver: WebDriver) : BasePage(driver) {
    private val locationSelector = By.id("filter-by-location")
    private val departmentSelector = By.id("filter-by-department")

    private val positions = By.cssSelector("div.position-list-item")
    private val positionTitle = By.cssSelector("p.position-title")
    private val positionDepartment = By.cssSelector("span.position-department")
    private val positionLocation = By.cssSelector("div.position-location")
    private val viewRoleLink = By.tagName("a")

    fun selectLocation(text: String) {
        selectInputOption(locationSelector, text)
    }

    fun assertDepartmentSelected(option: String) {
        assertText(find(departmentSelector).findElement(By.cssSelector("option[selected='selected']")), option)
    }

    fun assertPositions(expectedPositions: Array<Position>) {
        val positionsElements = waitForElementsVisible(positions)
        assertElementsLength(positionsElements, expectedPositions.size)
        for (i in expectedPositions.indices) {
            val expectedPosition = expectedPositions[i]
            val positionElement = positionsElements[i]
            scrollTo(positionElement)
            waitForAnimationToStop(positionElement)
            assertText(positionElement.findElement(positionTitle), expectedPosition.text)
            assertText(positionElement.findElement(positionDepartment), expectedPosition.categories.team)
            assertText(positionElement.findElement(positionLocation), expectedPosition.categories.location)
            assertHref(positionElement.findElement(viewRoleLink), expectedPosition.hostedUrl)
        }
    }

}