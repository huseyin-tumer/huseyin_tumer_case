package utils

import java.io.File

object FileUtil {

    fun readJsonFile(filePath: String): String {
        return File(filePath).readText()
    }
    
}