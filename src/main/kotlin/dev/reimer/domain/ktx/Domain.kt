package dev.reimer.domain.ktx

@JvmInline
value class Domain(
    val name: String
) {

    constructor(labels: Iterable<String>) : this(labels.joinToString(DELIMITER.toString()))

    companion object {
        const val DELIMITER = '.'
        private val LIST by lazy { PublicSuffixLists.get() }

        private fun String.split() = split(DELIMITER)
    }

    val length get() = name.length

    override fun toString() = name

    /**
     * Get the domain's labels, which are separated by [`.`][DELIMITER].
     */
    val labels get() = name.split()

    /**
     * [Public Suffix](https://publicsuffix.org/)
     *
     * For most cases, [suffix] is more appropriate and less error prone.
     */
    val publicSuffix
        get() =
            LIST.getPublicSuffix(name) ?: error("Domain '$name' doesn't have a Public Suffix.")

    /**
     * The [Public Suffix](https://publicsuffix.org/),
     * last domain part (opinionated),
     * or an empty string, if the domain consists of a single [label][labels]
     */
    val suffix get() = LIST.getPublicSuffix(name) ?: labels.run { if (size > 1) last() else "" }

    /**
     * This domain's [suffix] labels, which are separated by [`.`][DELIMITER].
     */
    val suffixLabels get() = suffix.split()

    /**
     * The top-level domain (TLD),
     * i.e., the domain, consisting of only the [suffix],
     * or the whole [name] if the suffix is empty.
     */
    val topLevel get() = suffix.takeUnless(String::isEmpty)?.let(::Domain) ?: this

    /**
     * The registrable domain
     */
    val registrable: Domain
        get() {
            val registrableName = LIST.getRegistrableDomain(name)
                ?: error("Domain '$name' does not contain a registrable domain.")
            return Domain(registrableName)
        }

    private val nameWithoutSuffix get() = name.removeSuffix(DELIMITER + suffix)

    /**
     * The domain name, stripped from the [prefix], if any.
     */
    val root get() = if (hasPrefix) nameWithoutSuffix.substringAfterLast(DELIMITER) + DELIMITER + suffix else name

    /**
     * This domain's [root] labels, which are separated by [`.`][DELIMITER].
     */
    val rootLabels get() = root.split()

    /**
     * This domain's prefix/sub-domain.
     */
    val prefix get() = nameWithoutSuffix.substringBeforeLast(DELIMITER, "")

    /**
     * This domain's prefix/sub-domain labels, which are separated by [`.`][DELIMITER].
     */
    val prefixLabels get() = prefix.split()

    val hasPrefix get() = prefix.isNotEmpty()

    fun stripSubDomain() = if (hasPrefix) Domain(name.substringAfter(DELIMITER)) else this

    fun stripSubDomains() =
        if (hasPrefix) Domain(nameWithoutSuffix.substringAfterLast(DELIMITER) + DELIMITER + suffix) else this
}