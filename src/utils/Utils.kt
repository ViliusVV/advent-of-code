package utils

public fun List<Any>.stringify(): String {
    return this.joinToString("\n")
}

public fun <K, V> Map<K, V>.stringify(): String {
    return this.entries.joinToString("\n") { "${it.key} -> ${it.value}" }
}

fun String.toLongs(): List<Long> {
    return this.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
}

fun String.toConcatLong(): Long {
    return this.split(" ").filter { it.isNotEmpty() }.joinToString("").toLong()
}
fun <K, V> Map<K, V>.printIt() {
    println(this.stringify())
}

fun List<Any>.printIt() {
    println(this.stringify())
}

fun Any?.printIt() {
    println(this)
}

fun getCurrentDayPackage(offset: Int = 0) = getCurrentPackage(1 + offset).split(".").dropLast(1).joinToString(".")
fun getCurrentPackage(offset: Int = 0): String {
    val stackTrace = Thread.currentThread().stackTrace
    val caller = stackTrace[2 + offset]
    return caller.className.split(".").dropLast(1).joinToString(".")
}