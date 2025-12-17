package utils

object TextUtil {
    fun textToSingleLine(text: String): String {
        return text.replace(Regex("\\s+"), " ").trim()
    }
}