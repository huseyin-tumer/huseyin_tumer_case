package models

data class DifferentiatorsItem(
    val title: String,
    val text: String? = null,
    val image: String
)

val initialDifferentiators = arrayOf(
    DifferentiatorsItem(
        title = "The three promises that set us apart",
        image = "https://insiderone.com/assets/media/2025/11/Marketer.webp"
    ),
    DifferentiatorsItem(
        title = "The three promises that set us apart\nBe first",
        image = "https://insiderone.com/assets/media/2025/11/Marketer.webp",
        text = """
            Insider One keeps you ahead with the most
            complete AI-native customer engagement
            platform, fueled by the world’s most ambitious
            product roadmap, built to move faster than the
            market. Always first. Always forward.
        """.trimIndent()
    ),
    DifferentiatorsItem(
        title = "The three promises that set us apart\nBe first\nBe focused",
        image = "https://insiderone.com/assets/media/2025/11/Marketer-1.webp",
        text = """
            The Insider One Advantage™ delivers an
            unparalleled vendor experience from white-glove
            migration to rapid onboarding, reducing total cost
            of ownership, and accelerating time to value. Break
            free from everything that holds you back.
        """.trimIndent()
    ),
    DifferentiatorsItem(
        title = "The three promises that set us apart\nBe first\nBe focused\nBe progressive",
        image = "https://insiderone.com/assets/media/2025/12/3.png",
        text = """
                At the heart of Insider One is the Growth Makers™
                Club. When exceptional minds collide, proven
                strategies emerge, results get shared, and
                learnings are shortcut. You think bigger and
                execute bolder. Here, every connection
                compounds your potential.
            """.trimIndent()
    ),
)