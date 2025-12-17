package tests

import infrastructure.BaseTest
import models.Position
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pages.CareerPage
import pages.MainPage
import pages.OpenPositionsPage
import utils.FileUtil

class CareersTests : BaseTest() {

    private lateinit var mainPage: MainPage

    @BeforeEach
    fun initPage() {
        navigateToPath("/careers/quality-assurance/")
        mainPage = MainPage(driver)
        mainPage.acceptAllCookies()

        CareerPage(driver).apply {
            assertPageLoaded()
            clickSeeAllButton()
        }
    }

    @Test
    fun `filter quality assurance`() {
        //TODO inform the bug, there are Istanbul and Istanbul, Turkiye options
        OpenPositionsPage(driver).apply {
            val leverPositionsUrl = "api.lever.co/v0/postings/insiderone?mode=json&team=Quality%20Assurance"
            networkInterceptor.startListening()

            mainPage.acceptAllCookies()
            assertUrlPathEndsWith("/open-positions/?department=qualityassurance")

            var positionsResponse = networkInterceptor.stopListeningUntilRequestLoaded(leverPositionsUrl)
            var positions = Position.parsePositions(positionsResponse)
            assertDepartmentSelected("Quality Assurance")
            assertPositions(positions) // asserts default loaded quality assurance positions

            networkInterceptor.startListening()
            selectLocation("Istanbul, Turkiye")
            positionsResponse = networkInterceptor.stopListeningUntilRequestLoaded(leverPositionsUrl)
            positions = Position.parsePositions(positionsResponse)
            assertPositions(positions) // asserts istanbul turkey located quality assurance positions
            assertPositions(
                titleContains = "Quality Assurance",
                department = "Quality Assurance",
                location = "Istanbul, Turkiye"
            )

        }
    }

    @Test
    fun `filter quality assurance by mocking`() {
        OpenPositionsPage(driver).apply {
            val mockBody = FileUtil.readJsonFile("src/main/resources/data/mock/positions.json")
            val urlPart = "api.lever.co/v0/postings/insiderone"
            driver.get("https://insiderone.com/careers/open-positions/?department=qualityassurance")
            networkInterceptor.mockApiResponse("GET", urlPart, mockBody, 200)

            mainPage.acceptAllCookies()
            assertUrlPathEndsWith("/open-positions/?department=qualityassurance")

            val positions = Position.parsePositions(mockBody)
            assertPositions(positions) // asserts istanbul turkey located quality assurance positions

        }
    }

}