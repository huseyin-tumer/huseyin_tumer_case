package utils

import org.awaitility.Awaitility
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.devtools.HasDevTools
import org.openqa.selenium.devtools.v141.fetch.Fetch
import org.openqa.selenium.devtools.v141.fetch.model.HeaderEntry
import org.openqa.selenium.devtools.v141.network.Network
import org.openqa.selenium.devtools.v141.network.model.RequestId
import org.openqa.selenium.devtools.v141.network.model.RequestWillBeSent
import org.openqa.selenium.devtools.v141.network.model.ResourceType
import org.openqa.selenium.devtools.v141.network.model.ResponseReceived
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.RemoteWebDriver
import java.time.Duration
import java.util.Base64
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class NetworkInterceptor(private val driver: WebDriver) {

    private val networkLogs = ConcurrentHashMap<String, NetworkLog>()

    fun startListening() {
        if (driver !is HasDevTools) {
            throw UnsupportedOperationException("Driver must implement HasDevTools")
        }
        networkLogs.clear()
        val devTools = (driver as HasDevTools).devTools
        devTools.createSession()
        devTools.send(
            Network.enable(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
            )
        )
        devTools.send(Network.setCacheDisabled(true))

        devTools.addListener(Network.requestWillBeSent()) { request: RequestWillBeSent ->
            val type = request.type.orElse(null)
            if (type == ResourceType.FETCH || type == ResourceType.XHR) {
                networkLogs[request.requestId.toString()] = NetworkLog(
                    id = request.requestId,
                    url = request.request.url,
                    method = request.request.method,
                    requestHeaders = request.request.headers.toJson()
                )
            }
        }

        devTools.addListener(Network.responseReceived()) { response: ResponseReceived ->
            val log = networkLogs[response.requestId.toString()]
            if (log != null) {
                log.responseStatus = response.response.status
                log.responseHeaders = response.response.headers.toJson()
                // Optionally try to fetch body here instantly, or lazy load.
                // Fetching body here might fail if loading not finished.
                // Usually safer to fetch when needed or wait for loadingFinished event.
                // For now, we update status.
            }
        }

        devTools.addListener(Network.loadingFinished()) { event ->
            val log = networkLogs[event.requestId.toString()]
            if (log != null) {
                log.isCompleted = true
            }
        }
    }

    fun stopListeningUntilRequestLoaded(urlPart: String, timeoutInSeconds: Long = 25): NetworkLog {
        if (driver !is HasDevTools) {
            throw UnsupportedOperationException("Driver must implement HasDevTools")
        }
        val devTools = (driver as HasDevTools).devTools

        try {
            var foundLog: NetworkLog? = null

            Awaitility.await()
                .atMost(Duration.ofSeconds(timeoutInSeconds))
                .pollInterval(Duration.ofMillis(100))
                .until {
                    foundLog = networkLogs.values.find {
                        it.url.contains(urlPart) && (it.isCompleted || it.responseStatus != null)
                    }
                    foundLog != null && foundLog!!.isCompleted
                }

            // Once found, we can try to fetch the body
            if (foundLog != null) {
                try {
                    // We might need to wait slightly for body to be available if it's large,
                    // but responseReceived usually means headers are there.
                    // LoadingFinished is the proper event for body availability.
                    // However, let's try fetching.
                    val responseBodyInfo = devTools.send(Network.getResponseBody(foundLog!!.id))
                    foundLog!!.responseBody = if (responseBodyInfo.base64Encoded) {
                        String(Base64.getDecoder().decode(responseBodyInfo.body))
                    } else {
                        responseBodyInfo.body
                    }
                } catch (e: Exception) {
                    println("Failed to fetch body for ${foundLog!!.url}: ${e.message}")
                }
                return foundLog!!
            } else {
                // Determine if we found it but it didn't complete
                val partialLog = networkLogs.values.find { it.url.contains(urlPart) }
                if (partialLog != null) {
                    throw RuntimeException("Found request for $urlPart but it did not complete loading within ${timeoutInSeconds}s. Status: ${partialLog.responseStatus}")
                }
                throw RuntimeException("Strict timeout reached: Request matching '$urlPart' not found.")
            }

        } finally {
            // Stop listening as requested
            devTools.clearListeners()
            // Optional: Disable network to save overhead, if we really want to "stop"
            // devTools.send(Network.disable())
        }
    }

    fun waitForResponse(urlPart: String, timeoutInSeconds: Long = 10): String {
        return when (driver) {
            is ChromeDriver -> waitForResponseChrome(urlPart, timeoutInSeconds)
            is FirefoxDriver -> waitForResponseFirefox(urlPart, timeoutInSeconds)
            is RemoteWebDriver -> {
                if (driver is HasDevTools) {
                    waitForResponseChrome(urlPart, timeoutInSeconds)
                } else {
                    throw UnsupportedOperationException("RemoteWebDriver must implement HasDevTools for network interception")
                }
            }

            else -> throw UnsupportedOperationException("Driver type not supported for network interception: ${driver::class.java.name}")
        }
    }

    private fun waitForResponseChrome(urlPart: String, timeoutInSeconds: Long): String {
        val devTools = (driver as HasDevTools).devTools
        devTools.createSession()
        devTools.send(
            Network.enable(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
            )
        )
        devTools.send(Network.setCacheDisabled(true))

        val latch = CountDownLatch(1)
        var requestId: RequestId? = null
        val listener = { response: ResponseReceived ->
            if (response.response.url.contains(urlPart)) {
                requestId = response.requestId
                latch.countDown()
            }
        }

        devTools.addListener(Network.responseReceived(), listener)

        try {
            val result = latch.await(timeoutInSeconds, TimeUnit.SECONDS)
            if (!result) {
                throw RuntimeException("Timeout waiting for response matching: $urlPart")
            }
            val responseBodyInfo = devTools.send(Network.getResponseBody(requestId!!))
            return if (responseBodyInfo.base64Encoded) {
                String(Base64.getDecoder().decode(responseBodyInfo.body))
            } else {
                responseBodyInfo.body
            }
        } finally {
            // devTools.clearListeners()
        }
    }

    private fun waitForResponseFirefox(urlPart: String, timeoutInSeconds: Long): String {
        if (driver is HasDevTools) {
            val devTools = (driver as HasDevTools).devTools
            devTools.createSession()
            devTools.send(
                Network.enable(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty()
                )
            )
            devTools.send(Network.setCacheDisabled(true))

            val latch = CountDownLatch(1)
            var requestId: RequestId? = null
            devTools.addListener(Network.responseReceived()) { response: ResponseReceived ->
                if (response.response.url.contains(urlPart)) {
                    requestId = response.requestId
                    latch.countDown()
                }
            }

            val result = latch.await(timeoutInSeconds, TimeUnit.SECONDS)
            if (!result) {
                throw RuntimeException("Timeout waiting for response matching: $urlPart in Firefox")
            }

            val responseBodyInfo = devTools.send(Network.getResponseBody(requestId!!))
            return if (responseBodyInfo.base64Encoded) {
                String(Base64.getDecoder().decode(responseBodyInfo.body))
            } else {
                responseBodyInfo.body
            }
        } else {
            throw UnsupportedOperationException("This FirefoxDriver instance does not support DevTools")
        }
    }

    private val mocks = ConcurrentHashMap<String, MockResponse>()
    private var isMockingStarted = false
    private var isSessionCreated = false

    private fun ensureSession() {
        if (!isSessionCreated) {
            val devTools = (driver as HasDevTools).devTools
            devTools.createSession()
            isSessionCreated = true
        }
    }
    data class MockResponse(val method: String, val body: String, val status: Int, val headers: List<HeaderEntry> = emptyList())

    fun mockResponse(method: String, urlPattern: String, body: String, status: Int = 200) {
        mocks[urlPattern] = MockResponse(method, body, status)
        startMocking()
    }

    private fun startMocking() {
        if (isMockingStarted) return
        if (driver !is HasDevTools) {
             throw UnsupportedOperationException("Driver must implement HasDevTools")
        }
        val devTools = (driver as HasDevTools).devTools
        ensureSession()
        
        // Explicitly enable Fetch for only XHR and FETCH requests
        val xhrPattern = org.openqa.selenium.devtools.v141.fetch.model.RequestPattern(
            Optional.of("*"), 
            Optional.of(org.openqa.selenium.devtools.v141.network.model.ResourceType.XHR), 
            Optional.empty()
        )
        val fetchPattern = org.openqa.selenium.devtools.v141.fetch.model.RequestPattern(
            Optional.of("*"), 
            Optional.of(ResourceType.FETCH),
            Optional.empty()
        )
        devTools.send(Fetch.enable(Optional.of(listOf(xhrPattern, fetchPattern)), Optional.empty()))

        devTools.addListener(Fetch.requestPaused()) { request ->
            try {
                val url = request.request.url
                val method = request.request.method
                println("Fetch listener: Paused $url")
                
                // Find matching mock
                val matchingMockEntry = mocks.entries.find { url.contains(it.key) }
                
                if (matchingMockEntry != null) {
                    val mock = matchingMockEntry.value
                    println("Found matching mock")

                    // Match method or OPTIONS
                    if (method.equals(mock.method, ignoreCase = true) || method == "OPTIONS") {
                         println("Fulfilling...")
                         val responseHeaders = listOf(
                            HeaderEntry("Access-Control-Allow-Origin", "*"),
                            HeaderEntry("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"),
                            HeaderEntry("Access-Control-Allow-Headers", "Content-Type, Authorization"),
                            HeaderEntry("Content-Type", "application/json")
                        )

                        if (method == "OPTIONS") {
                             devTools.send(Fetch.fulfillRequest(
                                request.requestId,
                                204,
                                Optional.of(responseHeaders),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty()
                            ))
                        } else {
                             devTools.send(Fetch.fulfillRequest(
                                request.requestId,
                                mock.status,
                                Optional.of(responseHeaders),
                                Optional.empty(),
                                Optional.of(Base64.getEncoder().encodeToString(mock.body.toByteArray())),
                                Optional.empty()
                            ))
                        }
                    } else {
                        println("Continuing...")
                        devTools.send(Fetch.continueRequest(
                            request.requestId,
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()
                        ))
                    }
                } else {
                    println("Continuing...")
                    devTools.send(Fetch.continueRequest(
                        request.requestId,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                    ))
                }
            } catch (e: ClassCastException) {
                // Ignore ClassCastException: LinkedHashMap cannot be cast to Void
            } catch (e: Exception) {
                println("Error in Fetch.requestPaused listener: ${e.message}")
            }
        }
        isMockingStarted = true
    }
    fun mockApiResponse(method: String, url: String, responseBody: String, status: Int) {
        if (driver !is HasDevTools) {
             throw UnsupportedOperationException("Driver must implement HasDevTools")
        }
        val devTools = (driver as HasDevTools).devTools
        devTools.createSession()

        val xhrPattern = org.openqa.selenium.devtools.v141.fetch.model.RequestPattern(
            Optional.of("*"), 
            Optional.of(ResourceType.XHR), 
            Optional.empty()
        )
        val fetchPattern = org.openqa.selenium.devtools.v141.fetch.model.RequestPattern(
            Optional.of("*"), 
            Optional.of(ResourceType.FETCH), 
            Optional.empty()
        )
        devTools.send(Fetch.enable(Optional.of(listOf(xhrPattern, fetchPattern)), Optional.empty()))

        devTools.addListener(Fetch.requestPaused()) { request ->
            try {
                if (request.request.url.contains(url) && (request.request.method.equals(method, ignoreCase = true) || request.request.method == "OPTIONS")) {
                    val responseHeaders = listOf(
                        HeaderEntry("Access-Control-Allow-Origin", "*"),
                        HeaderEntry("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"),
                        HeaderEntry("Access-Control-Allow-Headers", "Content-Type, Authorization"),
                        HeaderEntry("Content-Type", "application/json")
                    )

                    if (request.request.method == "OPTIONS") {
                        devTools.send(Fetch.fulfillRequest(
                            request.requestId,
                            204,
                            Optional.of(responseHeaders),
                            Optional.empty(),
                            Optional.empty(),
                            Optional.empty()
                        ))
                    } else {
                        devTools.send(Fetch.fulfillRequest(
                            request.requestId,
                            status,
                            Optional.of(responseHeaders),
                            Optional.empty(),
                            Optional.of(Base64.getEncoder().encodeToString(responseBody.toByteArray())),
                            Optional.empty()
                        ))
                    }
                } else {
                    devTools.send(Fetch.continueRequest(
                        request.requestId,
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty(),
                        Optional.empty()
                    ))
                }
            } catch (e: ClassCastException) {
                // Ignore ClassCastException
            } catch (e: Exception) {
                println("Error in mockApiResponse listener: ${e.message}")
            }
        }
    }
}
