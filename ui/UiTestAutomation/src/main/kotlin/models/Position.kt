package models

import infrastructure.Common


data class Category(
    val location: String,
    val team: String,
)

data class Position(
    val text: String,
    val workplaceType: String,
    val hostedUrl: String,
    val categories: Category,
) {
    companion object {
        fun parsePositions(text: String): Array<Position> {
            return Common.GSON.fromJson(text, Array<Position>::class.java)
        }
    }
}