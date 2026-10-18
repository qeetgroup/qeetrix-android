package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.upstream.UpstreamSync
import java.io.File
import kotlin.system.exitProcess

private const val USAGE = """qeetrix-gen <command> --root <repository>

Commands:
  sync [--check]  Fetch the artifacts pinned in Upstream/manifest.json, verify them and rewrite the lock.
                  With --check, compare upstream with the lock and change nothing. Network.
  generate        Regenerate every derived file from the snapshot. Offline, deterministic.
  verify          Fail if the snapshot differs from its lock or a generated file has drifted. Offline."""

fun main(arguments: Array<String>) {
    val args = arguments.toList()
    val command = args.firstOrNull()
    val rootIndex = args.indexOf("--root")
    if (command == null || rootIndex < 0 || rootIndex + 1 >= args.size) {
        System.err.println(USAGE)
        exitProcess(64)
    }
    val root = File(args[rootIndex + 1]).canonicalFile
    if (!File(root, "Upstream/manifest.json").exists()) {
        System.err.println("error: $root is not a qeetrix-android repository (no Upstream/manifest.json).")
        exitProcess(66)
    }
    val log: (String) -> Unit = { println(it) }
    try {
        when (command) {
            "sync" -> {
                UpstreamSync(root, log).run(check = "--check" in args)
            }

            "generate" -> {
                Generator(root, log).generate()
            }

            "verify" -> {
                Generator(root, log).verify()
            }

            else -> {
                System.err.println(USAGE)
                exitProcess(64)
            }
        }
    } catch (error: GeneratorError) {
        System.err.println("error: ${error.message}")
        exitProcess(1)
    }
}
