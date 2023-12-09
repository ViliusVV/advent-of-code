package utils

fun getCurrentDayPackage(offset: Int = 0) = getCurrentPackage(1 + offset).split(".").dropLast(1).joinToString(".")
fun getCurrentPackage(offset: Int = 0): String {
    val stackTrace = Thread.currentThread().stackTrace
    val caller = stackTrace[2 + offset]
    return caller.className.split(".").dropLast(1).joinToString(".")
}