package year2023.day04.part02

import utils.readInputFileLinesTrimmed
import utils.stringify
import kotlin.math.pow

data class Card(
    val id: Int,
    val winningNums: List<Int>,
    val realNums: List<Int>
) {
    private val matches by lazy {
        val cnt = realNums.count { it in winningNums }
        println("Card $id has $cnt matches")
        cnt
    }

    fun getNextCardIds(): List<Int> {
        if (matches == 0) {
            return emptyList()
        }

        // Generate next n cards after this one
        val nextCardIds = mutableListOf<Int>()
        for (i in 1..matches) {
            nextCardIds.add(id + i)
        }

        return nextCardIds
    }
}

fun main() {
    val inputLines = readInputFileLinesTrimmed("year2023/day04/input.data")

    val cards = parseCards(inputLines)
    println("Cards:\n${cards.stringify()}")

    val cardCounts = MutableList(cards.size) { 1L }

    for (i in cards.indices) {
        val card = cards[i]
        val cardCount = cardCounts[i]
        val nextCardIds = card.getNextCardIds()

        println("Card ${card.id} has count $cardCount")

        for (j in 1..cardCount) {
            for (nextCardId in nextCardIds) {
                cardCounts[nextCardId - 1]++
            }
        }
    }

    println("Card counts:\n${cardCounts.stringify()}")

    val totalCards = cardCounts.sum()
    println("Total card count: $totalCards")

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
