package year2023.day02.part02

import utils.readInputFileLinesTrimmed
import utils.stringify
import kotlin.math.max

data class Subset(
    var red: Int,
    var green: Int,
    var blue: Int
)
data class Game(
    val id: Int,
    val subsets: List<Subset>
)

val LIMIT_SUBSET = Subset(
    red=12,
    green=13,
    blue=14
)

fun main() {
    val lines = readInputFileLinesTrimmed("year2023/day02/input.data")

    val games = lines.map { parseGameLine(it) }

    println("Games: ${games.stringify()}")

    val gameToMinPower = games.associateWith { calculateGamePower(it) }
    println("Game to min power: ${gameToMinPower.stringify()}")

    val minPowerSum = gameToMinPower.values.sum()
    return println("Min power sum: $minPowerSum")
}

fun calculateGamePower(game: Game): Int {
    val minSubset = calculateMinGameSubset(game)
    return minSubset.red * minSubset.green * minSubset.blue
}

fun calculateMinGameSubset(game: Game): Subset {
    val minSubset  = Subset (0,0,0)

    for (s in game.subsets) {
        minSubset.red = max(minSubset.red, s.red)
        minSubset.green = max(minSubset.green, s.green)
        minSubset.blue = max(minSubset.blue, s.blue)
    }

    return minSubset
}



fun parseGameLine(line: String): Game {
    val id = line.substringAfter("Game ").substringBefore(":").toInt()
    val subsetsLine = line.substringAfter(": ").split(";")

    return Game(id, parseSubsets(subsetsLine))
}

fun parseSubsets(subsetsLine: List<String>): List<Subset> {
    return subsetsLine.map { parseSubset(it) }
}

fun parseSubset(subsetLine: String): Subset {
    val colorPairs = subsetLine.split(",").map { it.trim() }
    val subset = Subset(0, 0, 0)

     colorPairs.forEach {
        val pairSplit = it.split(" ")
        val count = pairSplit[0].toInt()
        val color = pairSplit[1]

         when (color) {
             "red" -> subset.red = count
             "green" -> subset.green = count
             "blue" -> subset.blue = count
         }
    }

    return subset
}





