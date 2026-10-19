package com.qeetgroup.qeetrix.gen.upstream

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.spi.InetAddressResolver
import java.net.spi.InetAddressResolver.LookupPolicy
import java.net.spi.InetAddressResolverProvider
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.stream.Stream

/**
 * Puts the addresses of a host that accept a connection first, so `sync` reaches a CDN from a network
 * that cannot reach every one of its addresses (seen with GitHub's content hosts). Java's HTTP client
 * connects to the first address a lookup returns and does not fall back to the next, as curl does.
 *
 * Each host's addresses are probed once — a TCP connection to port 443, all at once, two seconds at most —
 * and the order is remembered for the run. Nothing is removed: if no address answers, the lookup is
 * returned as the system gave it. Registered in `META-INF/services` for Java 18 and newer; earlier Java
 * ignores it. What is fetched is still checked against the integrity hashes and the lock.
 */
class ReachableFirstResolverProvider : InetAddressResolverProvider() {
    override fun get(configuration: Configuration): InetAddressResolver = ReachableFirstResolver(configuration.builtinResolver())

    override fun name(): String = "qeetrix-reachable-first"
}

private class ReachableFirstResolver(
    private val builtin: InetAddressResolver,
) : InetAddressResolver {
    private val reachable = ConcurrentHashMap<InetAddress, Boolean>()

    override fun lookupByName(
        host: String,
        lookupPolicy: LookupPolicy,
    ): Stream<InetAddress> {
        val addresses = builtin.lookupByName(host, lookupPolicy).toList()
        if (addresses.size < 2) return addresses.stream()
        probe(addresses.filter { !reachable.containsKey(it) })
        return addresses.sortedBy { if (reachable[it] == false) 1 else 0 }.stream()
    }

    override fun lookupByAddress(address: ByteArray): String = builtin.lookupByAddress(address)

    private fun probe(addresses: List<InetAddress>) {
        if (addresses.isEmpty()) return
        val pool = Executors.newFixedThreadPool(addresses.size)
        try {
            val results = addresses.map { address -> address to pool.submit<Boolean> { accepts(address) } }
            for ((address, result) in results) reachable[address] = result.get(PROBE_MILLIS + 500L, TimeUnit.MILLISECONDS)
        } finally {
            pool.shutdownNow()
        }
    }

    private fun accepts(address: InetAddress): Boolean =
        try {
            Socket().use { it.connect(InetSocketAddress(address, 443), PROBE_MILLIS) }
            true
        } catch (_: Exception) {
            false
        }

    private companion object {
        const val PROBE_MILLIS = 2_000
    }
}
