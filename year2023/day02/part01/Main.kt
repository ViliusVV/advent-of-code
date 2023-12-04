package day02.part01

import utils.readInputFileLinesTrimmed
import utils.stringify

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

    val filteredGames = filterGames(games)

    println("Filtered games: ${filteredGames.stringify()}")

    val gameIdSum = filteredGames.sumOf { it.id }

    println("Game id sum: $gameIdSum")
}

fun filterGames(games: List<Game>): List<Game> {
    fun validateSubset(subset: Subset): Boolean {
        return subset.red <= LIMIT_SUBSET.red &&
                subset.green <= LIMIT_SUBSET.green &&
                subset.blue <= LIMIT_SUBSET.blue
    }

    return games.filter {
        it.subsets.all {
            subset -> validateSubset(subset)
        }
    }

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





