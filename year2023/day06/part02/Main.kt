@file:Suppress("UnnecessaryVariable")

package day06.part02

import utils.readInputFileLinesTrimmed
import utils.toConcatLong
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
}

fun main() {
    val inputLines = readInputFileLinesTrimmed("year2023/day06/input.data")

    val duration = measureTime {
        solutionDumb(inputLines)
    }

    println("Completed in $duration")
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