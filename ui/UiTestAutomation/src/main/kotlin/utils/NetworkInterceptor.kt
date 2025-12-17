package utils

import infrastructure.DriverHelper
import infrastructure.Environment
import org.awaitility.Awaitility
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.devtools.HasDevTools
import org.openqa.selenium.devtools.v141.fetch.Fetch
import org.openqa.selenium.devtools.v141.fetch.model.HeaderEntry
import org.openqa.selenium.devtools.v141.fetch.model.RequestPattern
import org.openqa.selenium.devtools.v141.network.Network
import org.openqa.selenium.devtools.v141.network.model.RequestId
import org.openqa.selenium.devtools.v141.network.model.RequestWillBeSent
import org.openqa.selenium.devtools.v141.network.model.ResourceType
import org.openqa.selenium.devtools.v141.network.model.ResponseReceived
import org.openqa.selenium.remote.Augmenter
import java.time.Duration
import java.util.Base64
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap

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

class NetworkInterceptor(private var driver: WebDriver) {

    private val networkLogs = ConcurrentHashMap<String, NetworkLog>()

    private fun ensureJsHooksInstalled() {
        if (driver !is JavascriptExecutor) {
            throw UnsupportedOperationException("Driver must support JavaScript execution")
        }

        (driver as JavascriptExecutor).executeScript(
            """
            (function() {
              if (window.__networkHooksInstalled) {
                return;
              }
              window.__networkHooksInstalled = true;
              
              window.__networkLogs = window.__networkLogs || [];
              window.__networkMocks = window.__networkMocks || [];
              
              window.__pushNetworkLog = window.__pushNetworkLog || function(entry) {
                try {
                  window.__networkLogs.push(entry);
                } catch (e) {
                  // swallow
                }
              };
              
              function findMock(method, url) {
                try {
                  method = (method || 'GET').toUpperCase();
                  if (!window.__networkMocks) {
                    return null;
                  }
                  for (var i = 0; i < window.__networkMocks.length; i++) {
                    var m = window.__networkMocks[i];
                    if (!m) continue;
                    var mMethod = (m.method || 'GET').toUpperCase();
                    var mUrl = m.url || '';
                    if (mMethod === method && url.indexOf(mUrl) !== -1) {
                      return m;
                    }
                  }
                } catch (e) {
                  // swallow
                }
                return null;
              }
              
              // Patch fetch
              if (window.fetch && !window.__networkOriginalFetch) {
                window.__networkOriginalFetch = window.fetch;
                window.fetch = function() {
                  var args = Array.prototype.slice.call(arguments);
                  var input = args[0];
                  var url = (typeof input === 'string') ? input : (input && input.url) || '';
                  var options = args[1] || {};
                  var method = (options.method || 'GET').toUpperCase();
                  var startedAt = Date.now();
                  
                  var mock = findMock(method, url);
                  if (mock) {
                    var bodyText = mock.body || '';
                    try {
                      window.__pushNetworkLog({
                        url: url,
                        method: method,
                        status: mock.status,
                        headers: {},
                        body: bodyText,
                        completed: true,
                        type: 'fetch-mock',
                        startedAt: startedAt,
                        finishedAt: Date.now()
                      });
                    } catch (e) {
                      // swallow
                    }
                    return Promise.resolve(
                      new Response(bodyText, {
                        status: mock.status || 200,
                        headers: { 'Content-Type': 'application/json' }
                      })
                    );
                  }
                  
                  return window.__networkOriginalFetch.apply(this, args).then(function(response) {
                    try {
                      var clone = response.clone();
                      return clone.text().then(function(bodyText) {
                        window.__pushNetworkLog({
                          url: url,
                          method: method,
                          status: response.status,
                          headers: {}, // simplified
                          body: bodyText,
                          completed: true,
                          type: 'fetch',
                          startedAt: startedAt,
                          finishedAt: Date.now()
                        });
                        return response;
                      });
                    } catch (e) {
                      window.__pushNetworkLog({
                        url: url,
                        method: method,
                        status: response.status,
                        headers: {},
                        body: null,
                        completed: true,
                        type: 'fetch',
                        startedAt: startedAt,
                        finishedAt: Date.now()
                      });
                      return response;
                    }
                  });
                };
              }
              
              // Patch XHR
              (function() {
                if (!window.XMLHttpRequest || window.__networkXhrPatched) {
                  return;
                }
                window.__networkXhrPatched = true;
                
                var originalOpen = XMLHttpRequest.prototype.open;
                var originalSend = XMLHttpRequest.prototype.send;
                
                XMLHttpRequest.prototype.open = function(method, url) {
                  this.__networkInfo = {
                    method: method,
                    url: url,
                    startedAt: Date.now()
                  };
                  return originalOpen.apply(this, arguments);
                };
                
                XMLHttpRequest.prototype.send = function(body) {
                  var xhr = this;
                  var info = xhr.__networkInfo || {};
                  var method = (info.method || 'GET').toUpperCase();
                  var url = info.url || '';
                  var startedAt = info.startedAt || Date.now();
                  
                  var mock = findMock(method, url);
                  if (mock) {
                    setTimeout(function() {
                      try {
                        Object.defineProperty(xhr, 'status', {
                          configurable: true,
                          get: function() { return mock.status || 200; }
                        });
                        Object.defineProperty(xhr, 'responseText', {
                          configurable: true,
                          get: function() { return mock.body || ''; }
                        });
                        xhr.readyState = 4;
                        
                        try {
                          window.__pushNetworkLog({
                            url: url,
                            method: method,
                            status: mock.status,
                            headers: {},
                            body: mock.body || '',
                            completed: true,
                            type: 'xhr-mock',
                            startedAt: startedAt,
                            finishedAt: Date.now()
                          });
                        } catch (e) {
                          // swallow
                        }
                        
                        if (typeof xhr.onreadystatechange === 'function') {
                          xhr.onreadystatechange();
                        }
                        if (typeof xhr.onload === 'function') {
                          xhr.onload();
                        }
                      } catch (e) {
                        // swallow
                      }
                    }, 0);
                    return;
                  }
                  
                  var oldOnReadyStateChange = xhr.onreadystatechange;
                  xhr.onreadystatechange = function() {
                    try {
                      if (xhr.readyState === 4) {
                        window.__pushNetworkLog({
                          url: (xhr.__networkInfo && xhr.__networkInfo.url) || '',
                          method: (xhr.__networkInfo && xhr.__networkInfo.method) || '',
                          status: xhr.status,
                          headers: {},
                          body: xhr.responseText,
                          completed: true,
                          type: 'xhr',
                          startedAt: xhr.__networkInfo && xhr.__networkInfo.startedAt,
                          finishedAt: Date.now()
                        });
                      }
                    } catch (e) {
                      // swallow
                    }
                    
                    if (oldOnReadyStateChange) {
                      return oldOnReadyStateChange.apply(this, arguments);
                    }
                  };
                  
                  return originalSend.apply(this, arguments);
                };
              })();
            })();
            """.trimIndent()
        )
    }

    fun startListening() {

        val isDriverFirefox = DriverHelper.isFirefox(driver)

        if (isDriverFirefox || Environment.isGridExecution) {
            ensureJsHooksInstalled()
            (driver as JavascriptExecutor).executeScript("window.__networkLogs = [];")
        } else {
            driver = Augmenter().augment(driver)
            networkLogs.clear()
            val devTools = (driver as HasDevTools).devTools
            if (driver !is HasDevTools) {
                throw UnsupportedOperationException("Driver must implement HasDevTools")
            }
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

    }

    fun stopListeningUntilRequestLoaded(urlPart: String, timeoutInSeconds: Long = 25): String {

        val isDriverFirefox = DriverHelper.isFirefox(driver)

        if (isDriverFirefox || Environment.isGridExecution) {
            ensureJsHooksInstalled()

            val endTime = System.currentTimeMillis() + timeoutInSeconds * 1000

            while (System.currentTimeMillis() < endTime) {
                Thread.sleep(200)

                @Suppress("UNCHECKED_CAST")
                val logs = (driver as JavascriptExecutor).executeScript(
                    """
                if (!window.__networkLogs) {
                  return [];
                }
                return window.__networkLogs;
                """.trimIndent()
                ) as? List<Any?> ?: emptyList()

                val match = logs.firstOrNull { entry ->
                    val map = entry as? Map<*, *> ?: return@firstOrNull false
                    val url = map["url"]?.toString() ?: ""
                    val completed = map["completed"] as? Boolean ?: false
                    url.contains(urlPart) && completed
                } as? Map<*, *>

                if (match != null) {
                    val body = match["body"]?.toString()
                    if (body != null) {
                        return body
                    } else {
                        throw RuntimeException("Matched request for '$urlPart' but body was null or undefined.")
                    }
                }
            }

            throw RuntimeException("Timeout waiting for network request containing '$urlPart'")
        } else {

            driver = Augmenter().augment(driver)
            val devTools = (driver as HasDevTools).devTools

            if (driver !is HasDevTools) {
                throw UnsupportedOperationException("Driver must implement HasDevTools")
            }

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
                    return foundLog!!.responseBody!!
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
    }

    fun mockApiResponse(method: String, url: String, responseBody: String, status: Int) {

        val isDriverFirefox = DriverHelper.isFirefox(driver)

        if (isDriverFirefox || Environment.isGridExecution) {
            // Fallback: JS-level mocking on current page (works for Firefox without DevTools)
            if (driver !is JavascriptExecutor) {
                throw UnsupportedOperationException("Driver must support JavaScript execution")
            }

            ensureJsHooksInstalled()

            (driver as JavascriptExecutor).executeScript(
                """
            window.__networkMocks = window.__networkMocks || [];
            window.__networkMocks.push({
              method: (arguments[0] || 'GET').toUpperCase(),
              url: arguments[1] || '',
              body: arguments[2] || '',
              status: arguments[3] || 200
            });
            """.trimIndent(),
                method,
                url,
                responseBody,
                status
            )
        } else {
            driver = Augmenter().augment(driver)
            if (driver !is HasDevTools) {
                throw UnsupportedOperationException("Driver must implement HasDevTools")
            }
            val devTools = (driver as HasDevTools).devTools
            devTools.createSession()

            val xhrPattern = RequestPattern(
                Optional.of("*"),
                Optional.of(ResourceType.XHR),
                Optional.empty()
            )
            val fetchPattern = RequestPattern(
                Optional.of("*"),
                Optional.of(ResourceType.FETCH),
                Optional.empty()
            )
            devTools.send(Fetch.enable(Optional.of(listOf(xhrPattern, fetchPattern)), Optional.empty()))

            devTools.addListener(Fetch.requestPaused()) { request ->
                try {
                    if (request.request.url.contains(url) && (request.request.method.equals(
                            method,
                            ignoreCase = true
                        ) || request.request.method == "OPTIONS")
                    ) {
                        val responseHeaders = listOf(
                            HeaderEntry("Access-Control-Allow-Origin", "*"),
                            HeaderEntry("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS"),
                            HeaderEntry("Access-Control-Allow-Headers", "Content-Type, Authorization"),
                            HeaderEntry("Content-Type", "application/json")
                        )

                        if (request.request.method == "OPTIONS") {
                            devTools.send(
                                Fetch.fulfillRequest(
                                    request.requestId,
                                    204,
                                    Optional.of(responseHeaders),
                                    Optional.empty(),
                                    Optional.empty(),
                                    Optional.empty()
                                )
                            )
                        } else {
                            devTools.send(
                                Fetch.fulfillRequest(
                                    request.requestId,
                                    status,
                                    Optional.of(responseHeaders),
                                    Optional.empty(),
                                    Optional.of(Base64.getEncoder().encodeToString(responseBody.toByteArray())),
                                    Optional.empty()
                                )
                            )
                        }
                    } else {
                        devTools.send(
                            Fetch.continueRequest(
                                request.requestId,
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty(),
                                Optional.empty()
                            )
                        )
                    }
                } catch (e: ClassCastException) {
                    // Ignore ClassCastException
                } catch (e: Exception) {
                    println("Error in mockApiResponse listener: ${e.message}")
                }
            }
        }
    }
}
