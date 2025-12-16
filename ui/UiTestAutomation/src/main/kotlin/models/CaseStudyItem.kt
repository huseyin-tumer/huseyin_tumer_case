package models

import infrastructure.Common
import utils.FileUtil

data class CaseStudyItem(
    val image: String,
    val url: String,
    val number: String,
    val text: String,
    val customerLogo: String,
    val description: String,
    val subtext: String
)

val initialCaseStudyItems: Array<CaseStudyItem> =
    Common.GSON.fromJson(
        FileUtil.readJsonFile("src/main/resources/data/caseStudies.json"),
        Array<CaseStudyItem>::class.java
    )
