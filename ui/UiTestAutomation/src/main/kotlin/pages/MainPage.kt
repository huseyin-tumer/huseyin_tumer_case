package pages

import infrastructure.BasePage
import infrastructure.Environment
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.not
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import pageFunctions.SectionFunctions

class MainPage(driver: WebDriver) : BasePage(driver) {

    private val sectionFunctions = SectionFunctions(driver)

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


    private val capabilitiesHeader = By.cssSelector("div.homepage-capabilities-head div.title")
    private val capabilitiesDescription = By.cssSelector("div.homepage-capabilities-head div.description")

    private val aiHeaderTitle = By.cssSelector("div.homepage-insider-one-ai-head div.title")
    private val aiHeaderDescription = By.cssSelector("div.homepage-insider-one-ai-head div.description")

    private val channelTitle = By.cssSelector("div.homepage-channels-head .title")
    private val channelDescription = By.cssSelector("div.homepage-channels-head .description")

    private val caseStudySection = By.cssSelector("section.homepage-case-study")

    fun navigate() {
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
        sectionFunctions.scrollToSection("homepage-hero")
        shouldBeVisible(heroSection, "Home Page Block is not visible")
        shouldBeVisible(heroSectionHeader, "Home Page Header is not visible")
        shouldBeVisible(heroSectionDescription, "Home Page Description is not visible")
        shouldBeVisible(heroSectionEmail, "Input Email is not visible")
        shouldBeVisible(heroSectionRedirectButton, "Redirect Button is not visible")
    }

    fun assertSocialProofSectionLoaded() {
        sectionFunctions.scrollToSection("homepage-social-proof")
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
        sectionFunctions.scrollToSection("homepage-core-differentiators")
        sectionFunctions.assertDifferentiators()
    }

    fun assertCapabilitiesSection() {
        val section = sectionFunctions.scrollToSection("homepage-capabilities")
        //waitUtil.scrollPageUntilElementAttributeValueContains(headerNavigation, "class", "header-light")
        val capabilitiesSectionElement = section
        assertText(
            capabilitiesSectionElement.findElement(capabilitiesHeader),
            "One platform.\nEverything you need. Nothing you don’t."
        )
        assertText(
            capabilitiesSectionElement.findElement(capabilitiesDescription),
            "Insider One connects everything marketing and customer engagement teams need to get ahead and stay ahead."
        )
        sectionFunctions.assertCapabilities()
    }

    fun assertAiSection() {
        sectionFunctions.scrollToSection("homepage-insider-one-ai")
        assertText(aiHeaderTitle, "Meet Sirius AI™\nThe most complete AI solution for customer engagement")
        assertText(aiHeaderDescription, "Agentic AI. Generative AI. Predictive AI.")
        sectionFunctions.assertAi()
    }

    fun assertChannelsSection() {
        sectionFunctions.scrollToSection("homepage-channels")
        assertText(channelTitle, "Unmatched channel breadth\nfor unstoppable reach")
        assertText(
            channelDescription,
            "From SMS to WhatsApp, Email to Search, engage your customers wherever and however they choose."
        )
        sectionFunctions.assertChannels()
    }

    fun assertCaseStudySection() {
        sectionFunctions.scrollToSection("homepage-case-study")
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
        sectionFunctions.assertCaseStudies()
    }

    fun assertAnalystSection() {
        val analystSection = sectionFunctions.scrollToSection("homepage-analyst")
        assertText(
            analystSection.findElement(By.cssSelector(".title")),
            "Loved by brands, recognized by analysts"
        )
        assertText(
            analystSection.findElement(By.cssSelector(".description")), """
            The only vendor ranked #1 in every area marketing teams care about,
            from CDP to personalization to journey orchestration, all in one consolidated solution.
        """.trimIndent()
        )
        sectionFunctions.assertAnalysts()
    }

    fun assertIntegrationsSection() {
        val integrationSectionElement = sectionFunctions.scrollToSection("homepage-integrations")
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

    fun assertResourcesSection() {
        val section = sectionFunctions.scrollToSection("homepage-resources")
        assertText(section.findElement(By.cssSelector("h2.animated-text")), "ExploreResources")
        sectionFunctions.assertResources()
    }

}