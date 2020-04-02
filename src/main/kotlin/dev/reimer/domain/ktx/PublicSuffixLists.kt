package dev.reimer.domain.ktx

import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixList
import de.malkusch.whoisServerList.publicSuffixList.PublicSuffixListFactory
import java.io.InputStream

internal object PublicSuffixLists {

    private val RESOURCE_BASE = PublicSuffixLists::class.java
    private const val RESOURCE_NAME = "/domain/effective_tld_names.dat"

    private val list by lazy(PublicSuffixLists::load)

    fun get() = list

    private fun load(): PublicSuffixList {
        val resource: InputStream = RESOURCE_BASE.getResourceAsStream(RESOURCE_NAME)
        val factory = PublicSuffixListFactory()
        return factory.build(resource)
    }
}