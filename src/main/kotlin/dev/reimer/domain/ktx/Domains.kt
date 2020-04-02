package dev.reimer.domain.ktx

import java.net.URL

fun String.toDomain() = Domain(this)

val URL.domain: Domain
    get() {
        val host: String = host
        require(!host.contains('[')) { "Invalid hostname: $host" }
        val tld = host.substringAfterLast(Domain.DELIMITER)
        require(tld.toIntOrNull() == null) { "Invalid hostname: $host" }
        return Domain(host)
    }

val URL.domainOrNull: Domain?
    get() {
        val host: String = host
        if (host.contains('[')) return null
        val tld = host.substringAfterLast(Domain.DELIMITER)
        if (tld.toIntOrNull() != null) return null
        return Domain(host)
    }