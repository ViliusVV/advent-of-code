package utils

import java.io.File

private fun readInputFileLines(filename: String): List<String> {
    val inputStream = File(filename).inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }
    return inputString.split("\n")
}

private fun readInputFileLinesTrimmed(filename: String): List<String> {
    return readInputFileLines(filename).map { it.trim() }.filter { it.isNotEmpty() }
}

private fun packageToPath(packageName: String): String {
    return packageName.replace(".", "/")
}

fun readInputFile(trim: Boolean = true): List<String> {
    val packageDir = packageToPath(getCurrentDayPackage(3))

    return if (trim) {
        readInputFileLinesTrimmed("src/${packageDir}/input.data")
    } else {
        readInputFileLines("src/${packageDir}/input.data")
    }
}