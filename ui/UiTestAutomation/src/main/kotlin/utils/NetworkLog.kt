package utils

import org.openqa.selenium.devtools.v141.network.model.RequestId

data class NetworkLog(
    val id: RequestId,
    val url: String,
    val method: String,
    val requestHeaders: Map<String, Any>,
    var responseStatus: Int? = null,
    var responseHeaders: Map<String, Any>? = null,
    var responseBody: String? = null,
    var isCompleted: Boolean = false
)
