package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.tokens.DtcgSource
import com.qeetgroup.qeetrix.gen.tokens.TokenModel
import java.io.File

/** The real pinned snapshot in Upstream/, for the tests that read it. */
object Snapshot {
    val root: File = File(System.getProperty("qeetrix.root"))

    /** `Upstream/qeetrix-tokens/dtcg/`, relative path → content. */
    val dtcg: Map<String, String> by lazy {
        val directory = File(root, "Upstream/qeetrix-tokens/dtcg")
        directory
            .walkTopDown()
            .filter { it.isFile && it.extension == "json" }
            .associate { it.relativeTo(directory).invariantSeparatorsPath to it.readText() }
    }

    val source: DtcgSource by lazy { DtcgSource(dtcg) }

    /** The model with the component groups Upstream/manifest.json adopts. */
    fun model(adopted: Set<String> = setOf("sidebar", "timeline")): TokenModel = TokenModel(source, adopted)
}
