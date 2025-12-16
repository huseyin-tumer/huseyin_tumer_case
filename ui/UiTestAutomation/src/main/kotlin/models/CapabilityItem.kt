package models

data class CapabilityItem(val title: String, val description: String, val image: String, val url: String)

val initialCapabilities = arrayOf(
    CapabilityItem(
        title = "AI",
        description = "Leverage the combined power of Agentic AI, Generative AI, and Predictive AI with our AI-native capabilities",
        image = "https://insiderone.com/assets/media/2025/12/insider-one-ai.png",
        url = "/ai-overview/"
    ),
    CapabilityItem(
        title = "CDP",
        description = "Get a 360 view of customers to sharpen segmentation and activate customer data across every stage of the customer journey",
        image = "https://insiderone.com/assets/media/2025/12/cdp.png",
        url = "/customer-data-management/"
    ),
    CapabilityItem(
        title = "Personalization",
        description = "Great experiences happen when you know your customers inside out. Combine the power of unified customer date and Sirius AI™",
        image = "https://insiderone.com/assets/media/2025/12/personalization.png",
        url = "/ai-personalization/"
    ),
    CapabilityItem(
        title = "Journey Orchestration",
        description = "Orchestrate customer journeys across every touchpoint and channel",
        image = "https://insiderone.com/assets/media/2025/12/journey.png",
        url = "/customer-journey/orchestration/"
    ),
    CapabilityItem(
        title = "Reporting and Insights",
        description = "Make smarter decisions, faster. Build impactful campaigns and learn what's working with real-time insights and detailed performance reports",
        image = "https://insiderone.com/assets/media/2025/12/reporting.png",
        url = "/reporting-analytics/"
    ),
)