package utils

// =============== Printing ===============
fun List<Any>.stringify(): String {
    return this.joinToString("\n")
}

fun <K, V> Map<K, V>.stringify(): String {
    return this.entries.joinToString("\n") { "${it.key} -> ${it.value}" } }

fun <K, V> Map<K, V>.printIt() {
    println(this.stringify())
}

fun List<Any>.printIt() {
    println(this.stringify())
}

fun Any?.printIt() {
    println(this)
}

// Formatting
fun List<Any>.padRight(length: Int, char: Char = ' '): String {
    return this.joinToString("") { any -> any.toString().padEnd(length, char) }
}


// =============== Parsing ===============
fun String.toLongs(): List<Long> {
    return this.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
}

fun String.toConcatLong(): Long {
    return this.split(" ").filter { it.isNotEmpty() }.joinToString("").toLong()
}