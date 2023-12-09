@file:Suppress("UnnecessaryVariable")

package year2023.day06.part02

import utils.readInputFile
import utils.toConcatLong
import kotlin.math.sqrt
import kotlin.time.measureTime

data class Race(
    val holdTimeMs: Long,
    val distanceMm: Long
)

data class RaceData (
    val timeMs: Long,
    val recordMm: Long
) {

    // total distance = time * speed =
    // holdTime(speed) * remainingTime
    val allPossibleRaces by lazy {
        (0..timeMs).map {
            val holdTime = it
            val remainingTime = timeMs - holdTime
            val speed = holdTime
            val traveledDistance = remainingTime * speed
            Race(holdTime, traveledDistance)
        }
    }

    // using inequality:
    // (raceTime - holdTime) * holdTime > recordDistance simplifies to:
    // -holdTime^2 + raceTime * holdTime - recordDistance > 0
    val holdTimeToWin by lazy {
        // quadratic equation: ax^2 + bx + c = 0
        val a = -1.0
        val b = timeMs.toDouble()
        val c = -recordMm.toDouble()

        val delta = b * b - 4 * a * c
        val x1 = (-b + sqrt(delta)) / (2 * a)
        val x2 = (-b - sqrt(delta)) / (2 * a)

        // round range start to the next integer and range end to the previous integer
        val rangeStart = x1.toLong() + 1
        val rangeEnd = x2.toLong()

        // sanity check to make sure we don't go over the time limit
        val actualEnd = if (rangeEnd > timeMs) timeMs else rangeEnd

        actualEnd - rangeStart + 1
    }
}

fun main() {
    val inputLines = readInputFile()

    val duration = measureTime {
        solutionQuadEq(inputLines)
    }

    println("Completed in $duration")
}

fun solutionQuadEq(lines: List<String>) {
    val race = parseRaceData(lines)
    println("Races:\n${race}")

    val possibleWins = race.holdTimeToWin
    println("Possible wins: $possibleWins")
}

fun solutionDumb(lines: List<String>) {
    val race = parseRaceData(lines)
    println("Races:\n${race}")

    val allRaces = race.allPossibleRaces
//    println("Possible:\n${allRaces.stringify()}")

    val recordBreakingRaces = allRaces.filter { it.distanceMm > race.recordMm }
//    println("Filtered:\n${recordBreakingRaces.stringify()}")

    println("Count:\n${recordBreakingRaces.size}")
}



fun parseRaceData(lines: List<String>): RaceData {
    return RaceData(
        lines[0].split(":")[1].toConcatLong(),
        lines[1].split(":")[1].toConcatLong()
    )
}