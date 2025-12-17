package tests

import infrastructure.BaseTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pages.MainPage

class HomePageSectionTest : BaseTest() {

    private lateinit var mainPage: MainPage

    @BeforeEach
    fun initPage() {
        mainPage = MainPage(driver)
        mainPage.apply {
            navigate()
            acceptAllCookies()
            assertMainPageLoaded()
        }
    }

    @Test
    fun `all sections`() {
        mainPage.apply {
            assertHeroSectionLoaded()
            assertSocialProofSectionLoaded()
            assertDifferentiatorSection()
            assertCapabilitiesSection()
            assertAiSection()
            assertChannelsSection()
            assertCaseStudySection()
            assertAnalystSection()
            assertIntegrationsSection()
            assertResourcesSection()
        }
    }

    @Test
    fun `hero section`() {
        mainPage.assertHeroSectionLoaded()
    }

    @Test
    fun `social proof section`() {
        mainPage.assertSocialProofSectionLoaded()
    }

    @Test
    fun `differentiator section`() {
        mainPage.assertDifferentiatorSection()
    }

    @Test
    fun `capabilities section`() {
        mainPage.assertCapabilitiesSection()
    }

    @Test
    fun `ai section`() {
        mainPage.assertAiSection()
    }

    @Test
    fun `channels section`() {
        mainPage.assertChannelsSection()
    }

    @Test
    fun `case study section`() {
        mainPage.assertCaseStudySection()
    }

    @Test
    fun `analyst section`() {
        mainPage.assertAnalystSection()
    }

    @Test
    fun `analyst integrations`() {
        mainPage.assertIntegrationsSection()
    }

    @Test
    fun `analyst resources`() {
        mainPage.assertResourcesSection()
    }
}