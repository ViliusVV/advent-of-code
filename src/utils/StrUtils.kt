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

// =============== Parsing ===============
fun String.toLongs(): List<Long> {
    return this.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
}

fun String.toConcatLong(): Long {
    return this.split(" ").filter { it.isNotEmpty() }.joinToString("").toLong()
}