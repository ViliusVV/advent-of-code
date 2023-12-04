package day01.part01

import utils.readInputFileLines

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
    val usedLine = if (fromBack) line.reversed() else line
    for(c in usedLine) {
       if ('0'.code <= c.code && c.code <= '9'.code) {
           return c.code - '0'.code
       }
    }

    throw Exception("No digit found in line $line")
}

