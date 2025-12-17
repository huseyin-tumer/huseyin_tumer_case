package infrastructure

import java.util.Properties

object Environment {
    private val environmentProperties = Properties()

    init {
        val environment = System.getProperty("environment") ?: "prod"
        val fileName = "$environment.properties"
        val inputStream = this::class.java.classLoader.getResourceAsStream("environment/$fileName")
            ?: throw RuntimeException("$fileName not found in classpath")
        environmentProperties.load(inputStream)
    }

    val baseUrl: String by lazy {
        environmentProperties.getProperty("base.url") ?: throw RuntimeException("base.url not found in properties")
    }

    val isGridExecution: Boolean by lazy {
        val executionMode = System.getProperty("execution_mode") ?: System.getenv("execution_mode") ?: "local"
        executionMode.equals("grid", ignoreCase = true)
    }
}
