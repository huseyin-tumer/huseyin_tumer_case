package models

import infrastructure.Common
import utils.FileUtil

data class ChannelItem(
    val title: String,
    val description: String,
    val image: String,
    val url: String,
)

val initialChannelItems: Array<ChannelItem> =
    Common.GSON.fromJson(FileUtil.readJsonFile("src/main/resources/data/channels.json"), Array<ChannelItem>::class.java)