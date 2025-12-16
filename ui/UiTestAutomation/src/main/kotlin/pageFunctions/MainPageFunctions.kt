package pageFunctions

import infrastructure.BasePage
import infrastructure.shouldHaveText
import models.AiItem
import models.AnalystItem
import models.CapabilityItem
import models.CaseStudyItem
import models.ChannelItem
import models.DifferentiatorsItem
import models.ResourceItem
import models.initialAiItems
import models.initialAnalystItems
import models.initialCapabilities
import models.initialCaseStudyItems
import models.initialChannelItems
import models.initialDifferentiators
import models.initialResourceItems
import org.awaitility.Awaitility
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.time.Duration

class MainPageFunctions(driver: WebDriver) : BasePage(driver) {

    fun scrollToTitle(element: WebElement, untilTextContains: String?) {
        val shouldHaveText = untilTextContains != null
        lateinit var previousText: String
        if (shouldHaveText) {
            previousText = element.findElement(By.cssSelector("div.text p")).text.trim()
        }

        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .pollInterval(Duration.ofMillis(100))
            .until {
                scrollPage(0, 200)
                val found = element.findElement(By.cssSelector("div.title")).isDisplayed
                        && if (shouldHaveText) {
                    val currentText = element.findElement(By.cssSelector("div.text p")).text.trim()
                    currentText.isNotEmpty() && currentText != " "
                    currentText.contains(untilTextContains.takeLast(15)) && previousText != currentText
                } else true
                found
            }
    }

    private val capabilitiesSection = By.cssSelector("section.homepage-capabilities.light-theme")
    private val differentiatorItemsSelector = By.cssSelector("div.homepage-core-differentiators-body-item")
    private val capabilitiesButtons = By.cssSelector("div.homepage-capabilities-body-buttons>div")
    private val capabilitySlide = By.cssSelector("div.swiper-slide:not(.clone)")

    fun assertDifferentiators(differentiatorsItems: Array<DifferentiatorsItem> = initialDifferentiators) {
        assertElementsLength(differentiatorItemsSelector, differentiatorsItems.size)
        for (i in 0 until differentiatorsItems.size) {

            val differentiatorItem = findElements(differentiatorItemsSelector)[i]

            scrollToTitle(differentiatorItem, untilTextContains = differentiatorsItems[i].text)

            val title = differentiatorItem.findElement(By.cssSelector("div.title"))
            title.shouldHaveText(differentiatorsItems[i].title)

            if (differentiatorsItems[i].text != null) {
                val text = differentiatorItem.findElement(By.cssSelector("div.text p"))
                text.shouldHaveText(differentiatorsItems[i].text!!)
            }

            val img =
                differentiatorItem.findElement(By.cssSelector("div.homepage-core-differentiators-body-item-media img"))
            assertImgSource(img, differentiatorsItems[i].image)
        }
    }

    fun assertCapabilities(capabilities: Array<CapabilityItem> = initialCapabilities) {
        val section = find(capabilitiesSection)

        for (i in capabilities.indices) {
            val capability = capabilities[i]
            val capabilityButton = section.findElements(capabilitiesButtons)[i]
            assertText(capabilityButton, capability.title)
            capabilityButton.click()

            val capabilitySlideElement = section.findElements(capabilitySlide)[i]
            assertAttributeHasValue(capabilitySlideElement, "class", "swiper-slide-active")
            assertHref(
                capabilitySlideElement.findElement(By.cssSelector("a")),
                "https://insiderone.com${capability.url}"
            )
            assertImgSource(capabilitySlideElement.findElement(By.cssSelector("img")), capability.image)
            waitForText(
                capabilitySlideElement.findElement(By.cssSelector("div.slide-item-content-title")),
                capability.title
            )
            assertText(
                capabilitySlideElement.findElement(By.cssSelector("div.slide-item-content-text")),
                capability.description
            )

            /*
            val capability = capabilities[i]

            find(capabilitiesSection).findElements(capabilitiesButtons)[i].click()
            assertText(find(capabilitiesSection).findElements(capabilitiesButtons)[i], capability.title)

            val capabilitySlideElement = section.findElement(capabilitySlide)
            assertAttributeContains(capabilitySlideElement, "class", "swiper-slide-active")
            assertHref(capabilitySlideElement.findElement(By.cssSelector("a")), capability.url)
            assertImgSource(capabilitySlideElement.findElement(By.cssSelector("img")), capability.image)
            assertText(
                capabilitySlideElement.findElement(By.cssSelector("div.slide-item-content-title")),
                capability.title
            )
            assertText(
                capabilitySlideElement.findElement(By.cssSelector("div.slide-item-content-text")),
                capability.description
            )
             */

        }
    }

    private val aiContent = By.cssSelector("div.homepage-insider-one-ai-body-content-item")
    private val aiSubTitle = By.cssSelector("div.subtitle")
    private val aiTitle = By.cssSelector("div.title")
    private val aiDescription = By.cssSelector("div.description")
    private val action = By.cssSelector("div.action a")

    fun assertAi(aiItems: Array<AiItem> = initialAiItems) {
        val aiElements = findElements(aiContent)
        for (i in aiItems.indices) {
            val aiItem = aiItems[i]
            val aiItemElement = aiElements[i]
            aiItemElement.click()
            waitForAnimationToStop(aiItemElement)
            assertText(aiItemElement.findElement(aiSubTitle), aiItem.subTitle)
            assertText(aiItemElement.findElement(aiTitle), aiItem.title)
            assertText(aiItemElement.findElement(aiDescription), aiItem.description)
            assertText(aiItemElement.findElement(action), aiItem.actionText)
            assertHref(aiItemElement.findElement(action), "https://insiderone.com${aiItem.url}")
        }

    }

    private val channelSlideItems = By.cssSelector("div.homepage-channels-body div.swiper-slide")
    private val channelSlideItem = By.cssSelector("a .slide-item")
    private val channelSlideItemTitle = By.cssSelector("div.slide-item-title")
    private val channelSlideItemDescription = By.cssSelector("div.slide-item-description")
    private val channelSlideItemImage = By.cssSelector("div.slide-item-media img")
    private val channelSlideItemUrl = By.cssSelector("a")

    fun assertChannels(channelItems: Array<ChannelItem> = initialChannelItems) {
        for (i in channelItems.indices) {
            val channelItem = channelItems[i]
            val sortedIndex = (i % 3) * 3 + (i / 3)
            val channelElement = findElements(channelSlideItems)[sortedIndex]
            hover(channelElement)
            assertAttributeHasValue(channelElement.findElement(channelSlideItem), "class", "active")
            assertText(channelElement.findElement(channelSlideItemTitle), channelItem.title)
            assertText(channelElement.findElement(channelSlideItemDescription), channelItem.description)
            val image = channelElement.findElement(channelSlideItemImage)
            assertImgSource(image, channelItem.image)
            assertDisplayed(image)
            assertHref(channelElement.findElement(channelSlideItemUrl), "https://insiderone.com${channelItem.url}")
        }
    }

    private val caseStudies = By.cssSelector("div.homepage-case-study-body-item")
    private val caseStudyPictureImg = By.cssSelector("picture img")
    private val caseStudyPictureSource = By.cssSelector("picture source")
    private val caseStudyCustomerLogo = By.cssSelector("div.customer-logo img")
    private val caseStudyDescription = By.cssSelector("div.description")
    private val caseStudySubText = By.cssSelector("div.subtext")
    private val caseStudyInfoNumber = By.cssSelector("div.info-text .number")
    private val caseStudyInfoText = By.cssSelector("div.info-text .text")

    fun assertCaseStudies(caseStudyItems: Array<CaseStudyItem> = initialCaseStudyItems) {
        for (i in caseStudyItems.indices) {
            val caseStudyItem = caseStudyItems[i]
            val caseStudyElement = findElements(caseStudies)[i]
            if (i > 0)
                click(caseStudyElement)


            assertAttributeHasValue(caseStudyElement, "class", "active")

            assertHref(
                caseStudyElement.findElement(By.cssSelector(".action a")),
                "https://insiderone.com${caseStudyItem.url}"
            )

            waitForText(caseStudyElement.findElement(caseStudyInfoNumber), caseStudyItem.number)
            assertText(caseStudyElement.findElement(caseStudyInfoNumber), caseStudyItem.number)
            assertText(caseStudyElement.findElement(caseStudyInfoText), caseStudyItem.text)

            assertDisplayed(caseStudyElement.findElement(caseStudyPictureImg))
            assertImgSource(caseStudyElement.findElement(caseStudyPictureImg), caseStudyItem.image)

            assertAttribute(caseStudyElement.findElement(caseStudyPictureSource), "srcset", caseStudyItem.image)

            assertDisplayed(caseStudyElement.findElement(caseStudyCustomerLogo))
            assertImgSource(caseStudyElement.findElement(caseStudyCustomerLogo), caseStudyItem.customerLogo)

            assertText(caseStudyElement.findElement(caseStudyDescription), caseStudyItem.description)
            assertText(caseStudyElement.findElement(caseStudySubText), caseStudyItem.subtext)

        }
    }

    private val analystBody = By.cssSelector("div.homepage-analyst-body")
    private val analystButtons = By.cssSelector("div.homepage-analyst-body-nav-buttons a")
    private val analystsSlides = By.cssSelector("div.homepage-analyst-body div.swiper-slide")
    private val analystLogo = By.cssSelector("div.slide-item-content-logo img")
    private val analystContentInfo = By.cssSelector("div.slide-item-content-info .text")
    private val analystLink = By.cssSelector(".action a")
    private val analystCategories = By.cssSelector("div.slide-item-content-categories a")
    private val analystMedia = By.cssSelector("div.slide-item-media img")

    fun assertAnalysts(analystItems: Array<AnalystItem> = initialAnalystItems) {
        scrollTo(analystBody)
        val analystSlides = findElements(analystsSlides)
        for (i in analystItems.indices) {
            val analystItem = analystItems[i]
            val slideButton = findElements(analystButtons)[i]
            scrollTo(slideButton)
            assertText(slideButton, analystItem.buttonText)
            jsClick(slideButton)

            val analystSlide = analystSlides[i]
            scrollTo(By.cssSelector("div.homepage-analyst-body-slider.fadeInUp-scroll"))

            assertAttributeHasValue(analystSlide, "class", "swiper-slide-fully-visible")

            assertDisplayed(analystSlide.findElement(analystLogo))
            assertImgSource(analystSlide.findElement(analystLogo), analystItem.logo)
            assertText(analystSlide.findElement(analystContentInfo), analystItem.text)
            if (analystItem.url.isNotEmpty() && analystItem.actionText.isNotEmpty()) {
                assertText(analystSlide.findElement(analystLink), analystItem.actionText)
                assertHref(analystSlide.findElement(analystLink), "https://insiderone.com${analystItem.url}")
            }

            assertImgSource(analystSlide.findElement(analystMedia), analystItem.image)

            assertElementsLength(analystSlide.findElements(analystCategories), analystItem.categories.size)

            for (c in analystItem.categories.indices) {
                val category = analystItem.categories[c]
                scrollTo(analystSlide.findElements(analystCategories)[c])
                waitForText(
                    analystSlide.findElements(analystCategories)[c].findElements(By.cssSelector("span")).first(),
                    category.name
                )
                if (category.rating.isNotEmpty()) {
                    assertText(
                        analystSlide.findElements(analystCategories)[c].findElements(By.cssSelector("span")).last(),
                        category.rating
                    )
                }
            }

        }
    }

    private val resourceSection = By.cssSelector("section.homepage-resources")
    private val resourceSlides = By.cssSelector("div.swiper-slide")
    private val resourceSlideNavigationButtons = By.cssSelector("div.homepage-resources-navigation>div")
    private val resourceSlideImage = By.cssSelector("div.homepage-resources-card-image img")
    private val resourceSlideTitle = By.cssSelector("div.title")
    private val resourceSlideDescription = By.cssSelector("div.description")
    private val resourceSlideLink = By.cssSelector("a")

    fun assertResources(resourceItems: Array<ResourceItem> = initialResourceItems) {
        val resourceSectionElement = find(resourceSection)

        val previousButton = resourceSectionElement.findElements(resourceSlideNavigationButtons).first()
        assertAttribute(previousButton, "aria-disabled", "true")

        val nextButton = resourceSectionElement.findElements(resourceSlideNavigationButtons).last()
        assertAttribute(nextButton, "aria-disabled", "false")

        val slides = resourceSectionElement.findElements(resourceSlides)
        assertElementsLength(slides, resourceItems.size)
        scrollTo(slides[0])

        for (i in resourceItems.indices) {
            val resourceItem = resourceItems[i]
            val slideElement = slides[i]

            assertImgSource(slideElement.findElement(resourceSlideImage), resourceItem.imageUrl)
            assertText(slideElement.findElement(resourceSlideTitle), resourceItem.title)
            assertText(slideElement.findElement(resourceSlideDescription), resourceItem.description)
            assertHref(slideElement.findElement(resourceSlideLink), resourceItem.url)
            if (i < resourceItems.size - 3) {
                jsClick(nextButton)
                waitForAnimationToStop(slideElement)
            }

        }


    }


}