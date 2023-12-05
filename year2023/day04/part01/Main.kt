package day04.part01

import utils.readInputFileLinesTrimmed
import utils.stringify
import kotlin.math.pow

data class Card(
    val id: Int,
    val winningNums: List<Int>,
    val realNums: List<Int>
) {
    fun getPoints(): Int {
        val winningNumCount = realNums.count { it in winningNums}

        if (winningNumCount == 0) {
            return 0
        }

        // 2^(n-1)
        return 2.0.pow(winningNumCount - 1).toInt()
    }
}

fun main() {
    val inputLines = readInputFileLinesTrimmed("year2023/day04/input.data")

    val cards = parseCards(inputLines)
    println("Cards:\n${cards.stringify()}")

    val cardPointMap = cards.associate { it.id to it.getPoints() }
    println("Card points:\n${cardPointMap.stringify()}")

    val totalPoints = cardPointMap.values.sum()
    println("Total points: $totalPoints")
}

fun parseCards(lines: List<String>): List<Card> {
    return lines.map { parseCard(it) }
}

fun parseCard(string: String): Card {
    val gameParts = string.split(": ")
    val id = gameParts[0].split(" ").last().toInt()

    val nums = gameParts[1].split("| ")
    val winningNums = nums[0].split(" ").filter { it.isNotBlank() }.map { it.toInt() }
    val realNums = nums[1].split(" ").filter { it.isNotBlank() }.map { it.toInt() }

    return Card(id, winningNums, realNums)
}
