package utils

import java.io.File

fun readInputFileLines(filename: String): List<String> {
    val inputStream = File(filename).inputStream()
    val inputString = inputStream.bufferedReader().use { it.readText() }
    return inputString.split("\n")
}