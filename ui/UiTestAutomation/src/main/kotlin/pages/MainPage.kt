package pages

import infrastructure.BasePage
import infrastructure.Environment
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.not
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import pageFunctions.MainPageFunctions

private class NavBar(driver: WebDriver) : BasePage(driver) {
    private val platform = By.cssSelector("div.header-menu-item:not(.is-mobile)")
    private val industries = By.cssSelector("div.header-menu-item:not(.is-mobile)")
    private val customers = By.cssSelector("div.header-menu-item:not(.is-mobile)")
    private val resources = By.cssSelector("div.header-menu-item:not(.is-mobile)")

    fun assertNavBarLoaded() {
        shouldBeVisible(platform, "Platform is not visible")
        shouldBeVisible(industries, "Industries is not visible")
        shouldBeVisible(customers, "Customers is not visible")
        shouldBeVisible(resources, "Resources is not visible")
    }
}

class MainPage(driver: WebDriver) : BasePage(driver) {

    private val mainPageFunctions = MainPageFunctions(driver)

    private val acceptAllCookiesButton = By.cssSelector("a#wt-cli-accept-all-btn")
    private val logo = By.cssSelector("div.header-logo>a")
    private val navBar = NavBar(driver)

    private val heroSection = By.cssSelector("section.homepage-hero")
    private val heroSectionHeader = By.cssSelector("div.homepage-hero-content h1")
    private val heroSectionDescription = By.cssSelector("div.homepage-hero-content-description")
    private val heroSectionEmail = By.cssSelector("#email")
    private val heroSectionRedirectButton = By.cssSelector(".redirect-button")

    private val socialProofSection = By.cssSelector("section.homepage-social-proof")
    private val socialProofAnalystLogo = By.cssSelector("i.homepage-social-proof-analyst-logo img")
    private val socialProofRatingStarImg = By.cssSelector("div.homepage-social-proof-rating img")
    private val socialProofRatingStarText = By.cssSelector("div.homepage-social-proof-rating p")
    private val socialProofMiddleHeaderText = By.cssSelector("div.title.opacity-scroll.visible")
    private val socialProofSeeOurReviewsLink =
        By.cssSelector("div.homepage-social-proof-content-link.fadeInUp-scroll.visible a")


    private val headerNavigation = By.cssSelector("header#navigation")

    private val capabilitiesSection = By.cssSelector("section.homepage-capabilities.light-theme")
    private val capabilitiesHeader = By.cssSelector("div.homepage-capabilities-head div.title")
    private val capabilitiesDescription = By.cssSelector("div.homepage-capabilities-head div.description")

    private val aiSection = By.cssSelector("section.homepage-insider-one-ai.light-theme")
    private val aiHeaderTitle = By.cssSelector("div.homepage-insider-one-ai-head div.title")
    private val aiHeaderDescription = By.cssSelector("div.homepage-insider-one-ai-head div.description")


    fun open() {
        driver.get(Environment.baseUrl)
    }

    fun acceptAllCookies() {
        click(acceptAllCookiesButton)
    }

    fun assertMainPageLoaded() {
        assertThat("Could not get page title", driver.title, not(emptyString()))
        shouldBeVisible(logo, "Logo is not visible")
        navBar.assertNavBarLoaded()
    }

    fun assertHeroSectionLoaded() {
        scrollTo(heroSection)
        shouldBeVisible(heroSection, "Home Page Block is not visible")
        shouldBeVisible(heroSectionHeader, "Home Page Header is not visible")
        shouldBeVisible(heroSectionDescription, "Home Page Description is not visible")
        shouldBeVisible(heroSectionEmail, "Input Email is not visible")
        shouldBeVisible(heroSectionRedirectButton, "Redirect Button is not visible")
    }

    fun assertSocialProofSectionLoaded() {
        scrollTo(socialProofSection)
        shouldBeVisible(socialProofSection, "Social Proof is not visible")
        assertImgSource(socialProofAnalystLogo, "https://insiderone.com/assets/media/2025/12/Group-1.svg")
        assertImgSource(socialProofRatingStarImg, "https://insiderone.com/assets/media/2025/12/Stars.svg")
        assertText(socialProofRatingStarText, "4.9/5.0")
        assertText(socialProofMiddleHeaderText, "Trusted by 2,000+ customers")
        assertAttribute(
            socialProofSeeOurReviewsLink,
            "href",
            "https://www.gartner.com/reviews/market/multichannel-marketing-hubs/vendor/insider-1780331400/product/insider"
        )
    }

    fun assertDifferentiatorSection() {
        mainPageFunctions.assertDifferentiators()
    }

    fun assertCapabilitiesSection() {
        // header gets header-light value when scroll to top of capabilities section
        waitUtil.scrollPageUntilElementAttributeValueContains(headerNavigation, "class", "header-light")

        val capabilitiesSectionElement = find(capabilitiesSection)
        assertText(
            capabilitiesSectionElement.findElement(capabilitiesHeader),
            "One platform.\nEverything you need. Nothing you don’t."
        )
        assertText(
            capabilitiesSectionElement.findElement(capabilitiesDescription),
            "Insider One connects everything marketing and customer engagement teams need to get ahead and stay ahead."
        )

        mainPageFunctions.assertCapabilities()
    }

    fun assertAiSection() {
        scrollTo(aiSection)
        assertText(aiHeaderTitle, "Meet Sirius AI™\nThe most complete AI solution for customer engagement")
        assertText(aiHeaderDescription, "Agentic AI. Generative AI. Predictive AI.")
        mainPageFunctions.assertAi()
    }

    private val channelSection = By.cssSelector("section.homepage-channels.light-theme")
    private val channelTitle = By.cssSelector("div.homepage-channels-head .title")
    private val channelDescription = By.cssSelector("div.homepage-channels-head .description")

    fun assertChannelsSection() {
        scrollTo(channelSection)
        scrollTo(channelTitle)
        assertText(channelTitle, "Unmatched channel breadth\nfor unstoppable reach")
        assertText(
            channelDescription,
            "From SMS to WhatsApp, Email to Search, engage your customers wherever and however they choose."
        )
        mainPageFunctions.assertChannels()
    }

    private val caseStudySection = By.cssSelector("section.homepage-case-study")

    fun assertCaseStudySection() {
        scrollTo(caseStudySection)
        assertText(find(caseStudySection).findElement(By.cssSelector(".title")), "What brands achieve with Insider One")
        assertText(
            find(caseStudySection).findElement(By.cssSelector(".description")),
            "2,000+ world-leading brands trust Insider One to reach their peak potential and be unstoppable in customer engagement. Explore success stories."
        )
        assertText(find(caseStudySection).findElement(By.cssSelector(".action a")), "See all case studies")
        assertHref(
            find(caseStudySection).findElement(By.cssSelector(".action a")),
            "https://insiderone.com/case-studies"
        )
        mainPageFunctions.assertCaseStudies()
    }

    private val analystSection = By.cssSelector("section.homepage-analyst")

    fun assertAnalystSection() {
        scrollTo(caseStudySection) // scroll to previous section to prevent empty dom loading

        scrollTo(analystSection)

        assertText(
            find(analystSection).findElement(By.cssSelector(".title")),
            "Loved by brands, recognized by analysts"
        )
        assertText(
            find(analystSection).findElement(By.cssSelector(".description")), """
            The only vendor ranked #1 in every area marketing teams care about,
            from CDP to personalization to journey orchestration, all in one consolidated solution.
        """.trimIndent()
        )
        mainPageFunctions.assertAnalysts()
    }

    private val integrationsSection = By.cssSelector("section.homepage-integrations")

    fun assertIntegrationsSection() {
        val integrationSectionElement = find(integrationsSection)
        scrollTo(integrationSectionElement)
        assertText(
            integrationSectionElement.findElement(By.cssSelector(".title")),
            "IntegrateSeamlessly"
        )
        assertText(
            integrationSectionElement.findElement(By.cssSelector(".description")),
            "Connect everything, bring all your data, systems, and tools together with 100+ seamless integrations."
        )

        val exploreIntegrationsLink =
            integrationSectionElement.findElement(By.cssSelector("div.homepage-integrations-content-link>a"))
        assertText(exploreIntegrationsLink, "Explore Integrations")

        assertHref(exploreIntegrationsLink, "https://insiderone.com/integrations/")

        assertDisplayed(
            integrationSectionElement.findElement(By.cssSelector("img.desktop-image")),
            "https://insiderone.com/assets/media/2025/11/comp-9-media-scaled.png"
        )

    }

    //Use when you can not reach section element
    fun findSection(classIdentifier: String): WebElement {
        val sections = findElements(By.cssSelector("main.flexible-layout>section"))
        sections.forEach { section ->
            val classValues = section.getAttribute("class")!!.split(" ")
            if (classValues.contains(classIdentifier))
                scrollTo(section)
            return@forEach
        }
        return find(By.cssSelector("section.$classIdentifier"))
    }


    fun assertResourcesSection() {
        val section = findSection("homepage-resources")
        scrollTo(section) //scrolls to bottom of section
        waitForAnimationToStop(section)
        scrollIntoView(section) //scrolls to section
        assertText(section.findElement(By.cssSelector("h2.animated-text")), "ExploreResources")
        mainPageFunctions.assertResources()
    }

}