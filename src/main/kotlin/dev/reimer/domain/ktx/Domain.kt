package dev.reimer.domain.ktx

inline class Domain(
    val name: String
) {

    constructor(parts: Iterable<String>) : this(parts.joinToString(DELIMITER.toString()))

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
     * Get the top-level domain (TLD),
     * the domain, consisting of only the [suffix],
     * or the whole [name] if the suffix is empty.
     */
    val topLevel get() = Domain(suffix.takeUnless(String::isEmpty) ?: name)

    val registrable: Domain
        get() {
            val registrableName = LIST.getRegistrableDomain(name)
                ?: error("Domain '$name' is not registrable.")
            return Domain(registrableName)
        }

    private val nameWithoutSuffix get() = name.removeSuffix(DELIMITER + suffix)

    /**
     * The domain, stripped from its prefix.
     */
    val root: Domain
        get() {
            return Domain(nameWithoutSuffix.substringAfterLast(DELIMITER) + DELIMITER + suffix)
        }

    /**
     * This domain's prefix/sub-domain.
     */
    val prefix: String
        get() {
            return nameWithoutSuffix.substringBeforeLast(DELIMITER, "")
        }
}