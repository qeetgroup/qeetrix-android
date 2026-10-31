package com.qeetgroup.qeetrix

import org.json.JSONObject
import java.io.File

/** The repository the tests run in, and the generator's platform-neutral value table. */
internal object TestSupport {
    val root: File = File(System.getProperty("qeetrix.root") ?: error("qeetrix.root is not set; run the tests through Gradle."))

    /** `Documentation/Foundations/token-values.json` → `tokens`. */
    val tokenValues: JSONObject by lazy {
        JSONObject(File(root, "Documentation/Foundations/token-values.json").readText()).getJSONObject("tokens")
    }

    fun paths(kind: String): List<String> =
        tokenValues
            .keys()
            .asSequence()
            .filter {
                tokenValues.getJSONObject(it).getString("type") == kind
            }.toList()

    /** A number token's value, or a length's / duration's amount (`px`, `ms`), in the light scheme. */
    fun number(path: String): Double =
        when (val v = value(path)) {
            is Number -> v.toDouble()
            is JSONObject -> if (v.has("px")) v.getDouble("px") else v.getDouble("ms")
            else -> error("$path is not a number")
        }

    fun value(
        path: String,
        theme: String = "light",
    ): Any =
        tokenValues
            .getJSONObject(path)
            .getJSONObject("themes")
            .getJSONObject(theme)
            .get("value")
}
