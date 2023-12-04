package utils

public fun List<Any>.stringify(): String {
    return this.joinToString("\n")
}

public fun <K, V> Map<K, V>.stringify(): String {
    return this.entries.joinToString("\n") { "${it.key} -> ${it.value}" }
}