package year2023.day01.part02

import utils.readInputFileLines

val VALID_DIGIT_WORDS = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

fun main() {
    val lines = readInputFileLines("year2023/day01/input.data")

    val calibrationValues = lines.map { lineToCalibrationValue(it) }
    val masterCalibrationValue = calibrationValues.sum()


    println("Calibration value: $calibrationValues")

    println("Master calibration value: $masterCalibrationValue")
}

fun lineToCalibrationValue(line: String): Int {
    val firstDigit = extractFirstDigit(line, false)
    val secondDigit = extractFirstDigit(line, true)
    return (firstDigit * 10) + secondDigit
}

fun extractFirstDigit(line: String, fromBack: Boolean): Int {
    val indices = line.indices.let {
        if (fromBack) it.reversed() else it
    }

    for(cIdx in indices) {
        val c = line[cIdx]
        if ('0'.code <= c.code && c.code <= '9'.code) {
            return c.code - '0'.code
        } else {
            // TODO: This is very inefficient
            val probDigitWord = line.substring(cIdx).trim()
            for (digitWord in VALID_DIGIT_WORDS) {
                if (probDigitWord.startsWith(digitWord)) {
                    return VALID_DIGIT_WORDS.indexOf(digitWord) + 1
                }
            }
        }
    }

    throw Exception("No digit found in line $line")
}

