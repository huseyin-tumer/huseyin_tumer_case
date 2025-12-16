package models

import infrastructure.Common
import utils.FileUtil

data class AnalystItem(
    val buttonText: String,
    val logo: String,
    val text: String,
    val url: String,
    val actionText: String,
    val image: String,
    val categories: List<CategoryItem>
)

data class CategoryItem(
    val name: String,
    val rating: String
)

val initialAnalystItems: Array<AnalystItem> =
    Common.GSON.fromJson(
        FileUtil.readJsonFile("src/main/resources/data/analysts.json"),
        Array<AnalystItem>::class.java
    )
