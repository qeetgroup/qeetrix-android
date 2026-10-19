package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.components.ComponentScaffold
import com.qeetgroup.qeetrix.gen.upstream.UpstreamSync
import java.io.File
import kotlin.system.exitProcess

private const val USAGE = """qeetrix-gen <command> --root <repository>

Commands:
  sync [--check]  Fetch the artifacts pinned in Upstream/manifest.json, verify them and rewrite the lock.
                  With --check, compare upstream with the lock and change nothing. Network.
  generate        Regenerate every derived file from the snapshot. Offline, deterministic.
  verify          Fail if the snapshot differs from its lock or a generated file has drifted. Offline.
  new-component <Family> <Component> --category <ui.qeet.in group> [--slug <web slug>] [--dry-run]
                  Scaffold a component from Templates/Component. Never overwrites; --dry-run only lists
                  the changes."""

/** The value after [flag], if present. */
private fun option(
    flag: String,
    args: List<String>,
): String? = args.indexOf(flag).takeIf { it >= 0 && it + 1 < args.size }?.let { args[it + 1] }

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

            "new-component" -> {
                newComponent(root, args, log)
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

private fun newComponent(
    root: File,
    args: List<String>,
    log: (String) -> Unit,
) {
    val positional = args.drop(1).takeWhile { !it.startsWith("--") }
    val category = option("--category", args)
    if (positional.size != 2 || category == null) {
        throw GeneratorError("new-component needs <Family> <Component> --category <ui.qeet.in group>.\n$USAGE")
    }
    val scaffold = ComponentScaffold(root)
    val request = ComponentScaffold.Request(positional[0], positional[1], category, option("--slug", args))
    val changes = scaffold.plan(request)
    val newPackage = scaffold.introducesUndocumentedPackage(request)
    if ("--dry-run" in args) {
        for (change in changes) log("• would ${change.kind.verb} ${change.path}")
        return
    }
    scaffold.apply(changes)
    for (change in changes) log("✓ ${change.kind.past} ${change.path}")
    log("Next:")
    if (changes.any { it.path == ComponentScaffold.UPSTREAM_PATH }) log("  • run Scripts/generate-tokens for the component's tokens")
    if (newPackage) log("  • describe the new package in ${ComponentScaffold.MODULE_DOC_PATH}")
    log("  • implement the component, its tests and its sample page; record and review its screenshots")
}
