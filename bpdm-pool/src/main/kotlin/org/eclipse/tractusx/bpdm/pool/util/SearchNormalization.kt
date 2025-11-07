package org.eclipse.tractusx.bpdm.pool.util

import com.neovisionaries.i18n.CountryCode

/**
 * Utility functions for preparing textual values for normalized search columns.
 *
 * Normalization rules:
 * * lower-case the value
 * * trim leading/trailing whitespace and collapse internal whitespace
 * * convert German umlauts to their ASCII representations (ä->ae, ö->oe, ü->ue)
 * * convert German sharp s to "ss"
 */
object SearchNormalization {
    private val replacements = listOf(
        "ä" to "ae",
        "ö" to "oe",
        "ü" to "ue",
        "ß" to "ss"
    )

    fun normalize(input: String): String {
        var normalized = input.lowercase()
            .trim()
            .replace("\\s+".toRegex(), " ")

        replacements.forEach { (target, replacement) ->
            normalized = normalized.replace(target, replacement)
        }

        return normalized
    }

    fun normalizeOrNull(input: String?): String? = input?.let { normalize(it) }

    fun normalize(country: CountryCode): String = normalize(country.toString())
}
