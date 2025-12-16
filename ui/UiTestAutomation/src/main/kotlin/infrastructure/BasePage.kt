package infrastructure

import org.awaitility.Awaitility
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.`is`
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.interactions.WheelInput
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.FluentWait
import org.openqa.selenium.support.ui.Select
import utils.NetworkInterceptor
import utils.WaitUtil
import java.time.Duration

abstract class BasePage(protected val driver: WebDriver) {
    protected val waitUtil: WaitUtil by lazy { WaitUtil(driver) }
    val networkInterceptor: NetworkInterceptor by lazy { NetworkInterceptor(driver) }

    protected val wait: WebDriverWait = WebDriverWait(driver, Duration.ofSeconds(10))

    protected val fluentWait: FluentWait<WebDriver> = FluentWait(driver)
        .withTimeout(Duration.ofSeconds(10))
        .pollingEvery(Duration.ofMillis(500))
        .ignoring(StaleElementReferenceException::class.java)

    protected fun find(locator: By): WebElement {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator))!!
    }

    protected fun findElements(locator: By): List<WebElement> {
        wait.until(
            ExpectedConditions.and(
                ExpectedConditions.presenceOfAllElementsLocatedBy(locator),
            )
        )
        return driver.findElements(locator)
    }

    protected fun waitForElementsVisible(locator: By): List<WebElement> {
        wait.until(
            ExpectedConditions.and(
                ExpectedConditions.visibilityOfAllElements(findElements(locator)),
            )
        )
        return driver.findElements(locator)
    }



    protected fun shouldBeVisible(
        selector: By,
        message: String = "Element should be visible, selector: ${selector.toString()}"
    ) {
        assertThat(message, find(selector).isDisplayed, `is`(true))
    }

    protected fun click(locator: By) {
        find(locator).click()
    }

    protected fun click(element: WebElement) {
        wait.until(ExpectedConditions.elementToBeClickable(element))!!.click()
    }

    protected fun jsClick(element: WebElement) {
        val js = driver as JavascriptExecutor
        js.executeScript("arguments[0].click();", element)
    }

    protected fun assertSelectInputHasOption(locator: By, option: String) {
        val options = find(locator).findElements(By.tagName("option")).map { it.text }
        assertThat("option not found in select input options", options, hasItem(option))
    }

    protected fun selectInputOption(locator: By, option: String) {
        assertSelectInputHasOption(locator, option)
        Select(find(locator)).selectByVisibleText(option)
    }

    protected fun type(locator: By, text: String) {
        val element = find(locator)
        element.clear()
        element.sendKeys(text)
    }

    protected fun assertText(
        locator: By, text: String, message: String = """
        Element text assertion failed
        Selector $locator
    """.trimIndent()
    ) {
        assertThat(message, find(locator).text, equalTo(text))
    }

    protected fun waitForText(element: WebElement, text: String) {
        wait.until(ExpectedConditions.textToBePresentInElement(element, text))
    }

    protected fun assertText(
        element: WebElement,
        text: String,
        message: String = """
            Element text assertion failed
            Selector ${element.getSelector()}
        """.trimIndent()
    ) {
        assertThat(message, element.text, equalTo(text))
    }

    protected fun assertAttribute(
        locator: By,
        attribute: String,
        value: String,
        message: String = """
            Element attribute assertion failed
            Selector: $locator
            Attribute: $attribute
        """.trimIndent()
    ) {
        assertThat(message, find(locator).getAttribute(attribute), equalTo(value))
    }

    protected fun assertAttribute(
        element: WebElement,
        attribute: String,
        value: String,
        message: String = """
            Element attribute assertion failed
            Attribute: $attribute
        """.trimIndent()
    ) {
        assertThat(message, element.getAttribute(attribute), equalTo(value))
    }

    protected fun assertAttributeHasValue(
        element: WebElement,
        attribute: String,
        value: String,
        message: String = """
            Element attribute assertion failed
            Attribute: $attribute
        """.trimIndent()
    ) {
        val attributes = element.getAttribute(attribute)!!.split(" ")
        assertThat(message, attributes, hasItem(value))
    }

    protected fun assertHref(
        element: WebElement,
        value: String,
        message: String = """
            Element attribute href attribute failed
        """.trimIndent()
    ) {
        assertThat(message, element.getAttribute("href"), equalTo(value))
    }

    protected fun assertHref(
        locator: By,
        value: String,
        message: String = """
            Element attribute href attribute failed
        """.trimIndent()
    ) {
        assertThat(message, find(locator).getAttribute("href"), equalTo(value))
    }

    fun assertImgSource(
        locator: By,
        src: String,
        message: String = """
            Element img source assertion failed
            Selector $locator
        """.trimIndent()
    ) {
        assertAttribute(locator, "src", src, message)
    }

    fun assertImgSource(
        element: WebElement, src: String,
        message: String = """
            Element img source assertion failed
            Expected src: $src
        """.trimIndent()
    ) {
        assertAttribute(element, "src", src, message)
    }

    fun assertElementsLength(locator: By, length: Int, message: String? = null) {
        if (message == null)
            assertThat(findElements(locator).size, equalTo(length))
        else
            assertThat(message, findElements(locator).size, equalTo(length))
    }

    fun assertElementsLength(element: List<WebElement>, length: Int, message: String? = null) {
        if (message == null)
            assertThat(element.size, equalTo(length))
        else
            assertThat(message, element.size, equalTo(length))
    }

    fun assertDisplayed(
        element: WebElement,
        message: String = """
            Element img visibility assertion failed
        """.trimIndent()
    ) {
        assertThat(message, element.isDisplayed, `is`(true))
    }

    protected fun <T> retry(block: () -> T): T {
        var attempt = 0
        while (attempt < 3) {
            try {
                return block()
            } catch (e: StaleElementReferenceException) {
                attempt++
            }
        }
        throw StaleElementReferenceException("Failed after 3 retries")
    }

    protected fun swipeToElement(locator: By) {
        val element = wait.until(ExpectedConditions.presenceOfElementLocated(locator))
        (driver as JavascriptExecutor).executeScript("arguments[0].scrollIntoView(true);", element)
    }

    protected fun scrollTo(locator: By) {
        Actions(driver).scrollToElement(find(locator))
            .perform()
    }

    protected fun scrollTo(element: WebElement) {
        (driver as JavascriptExecutor).executeScript(
            "arguments[0].scrollIntoView({block: 'center', inline: 'center'});",
            element
        )
    }

    val js = driver as JavascriptExecutor
    protected fun scrollIntoView(element: WebElement) {
        js.executeScript("arguments[0].scrollIntoView(true);", element)
    }

    protected fun scrollPage(x: Int = 0, y: Int = 0) {
        Actions(driver).scrollByAmount(x, y).perform()
    }

    protected fun scrollOriginFromElement(element: WebElement, x: Int = 0, y: Int = 0) {
        Actions(driver).scrollFromOrigin(WheelInput.ScrollOrigin.fromElement(element), x, y).perform()
    }

    fun hover(element: WebElement) {
        scrollTo(element)
        Actions(driver).moveToElement(element).perform()
    }

    protected fun assertElementInViewport(element: WebElement): Boolean {
        val js = driver as JavascriptExecutor
        val isInViewport = js.executeScript(
            """
    var rect = arguments[0].getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
""", element
        ) as Boolean

        if (isInViewport) {
            println("Element is physically visible on screen!")
        }
        return isInViewport
    }

    fun getAttribute(locator: By, attribute: String): String {
        return find(locator).getAttribute(attribute)!!
    }

    fun waitForAnimationToStop(element: WebElement) {
        var lastRect = element.rect
        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(100))
            .until {
                val currentRect = element.rect
                if (lastRect == currentRect)
                    true
                else {
                    lastRect = element.rect
                    false
                }
            }
    }

    fun assertUrlPathEndsWith(endsWith: String) {
        assertThat(driver.currentUrl, Matchers.endsWith(endsWith))
    }

}