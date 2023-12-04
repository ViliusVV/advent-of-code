package utils

public fun List<Any>.stringify(): String {
    return this.joinToString("\n")
}