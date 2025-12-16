package models

import infrastructure.Common
import utils.FileUtil

data class ResourceItem(
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
)

val initialResourceItems: Array<ResourceItem> =
    Common.GSON.fromJson(FileUtil.readJsonFile("src/main/resources/data/resources.json"), Array<ResourceItem>::class.java)