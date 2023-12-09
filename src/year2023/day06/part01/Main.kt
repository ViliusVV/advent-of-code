@file:Suppress("UnnecessaryVariable")

package year2023.day06.part01

import utils.readInputFileLinesTrimmed
import utils.stringify
import utils.toLongs

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

    val races = parseRaceData(inputLines)
    println("Races:\n${races}")

    val racesMappedToPossibleRaces = races.associateWith { it.allPossibleRaces }
    println("Possible:\n${racesMappedToPossibleRaces.stringify()}")

    val filteredRecordBreaking = racesMappedToPossibleRaces.mapValues{
        it.value.filter { race -> race.distanceMm > it.key.recordMm }
    }

    println("Filtered:\n${filteredRecordBreaking.stringify()}")

    val counts = filteredRecordBreaking.mapValues { it.value.size }
    println("Counts:\n${counts.stringify()}")

    val combinations = counts.values.reduce { acc, i -> acc * i }
    println("Combinations: $combinations")
}



fun parseRaceData(lines: List<String>): List<RaceData> {
    val times = lines[0].split(":")[1].toLongs()
    val records = lines[1].split(":")[1].toLongs()

    return  times.mapIndexed { index: Int, l: Long -> RaceData(l, records[index]) }
}