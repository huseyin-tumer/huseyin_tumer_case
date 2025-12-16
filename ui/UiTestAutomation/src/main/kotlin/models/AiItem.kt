package models

data class AiItem(
    val subTitle: String,
    val title: String,
    val description: String,
    val url: String = "/ai-overview/",
    val actionText: String = "Explore Agent One",
)

val initialAiItems = arrayOf(
    AiItem(
        subTitle = "AGENTIC AI",
        title = "Autonomous agents for superior customer engagement",
        description = "Agent One™ brings together purpose-built AI agents to help you deliver superior customer engagement through emotionally resonant conversations and autonomous decision-making.",
    ),
    AiItem(
        subTitle = "GENERATIVE AI",
        title = "Put your marketing on autopilot with Sirius AI™",
        description = "Improve targeting precision using AI-powered predictive segments in real time to boost the relevance of every interaction",
    ),
    AiItem(
        subTitle = "PREDICTIVE AI",
        title = "Maximize impact with AI-powered precision",
        description = "Combine data from online and offline sources, like your CRM, POS, and contact centers, to segment users based on real-time events and rule-based triggers.",
    )
)