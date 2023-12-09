package year2023.day07.part01

import utils.printIt
import utils.readInputFileLinesTrimmed

typealias Card = String

interface HandType {
    fun isHandType(cards: List<Card>): Boolean
}

data class Hand (
    val cards: List<Card>,
    val bid: Long
): Comparable<Hand> {
    val highestHand by lazy { HAND_TYPES.last { it.isHandType(cards) } }
    val highestHandRank by lazy { HAND_TYPES.indexOf(highestHand) }

    override fun compareTo(other: Hand): Int {
        val handCompare = other.highestHandRank - this.highestHandRank
        if (handCompare != 0) {
            return handCompare
        }

        for(i in this.cards.indices) {
            val cardCompare = other.cards[i].getCardValue() - this.cards[i].getCardValue()
            if (cardCompare != 0) {
                return cardCompare
            }
        }

        return 0
    }
}

data object HighCard : HandType {
    override fun isHandType(cards: List<Card>): Boolean {
        return true
    }
}

data object OnePair: HandType {
    override fun isHandType(cards: List<Card>): Boolean {
        return cards.groupBy { it }.filter { it.value.size == 2 }.count() == 1
    }
}

data object TwoPairs: HandType {
    override fun isHandType(cards: List<Card>): Boolean {
        return cards.groupBy { it }.filter { it.value.size == 2 }.count() == 2
    }
}

data object ThreeOfAKind: HandType {
    override fun isHandType(cards: List<Card>): Boolean {
        return cards.groupBy { it }.filter {it.value.size == 3}. count() == 1
    }
}

data object FullHouse: HandType {
    override fun isHandType(cards: List<Card>): Boolean {
        val grouped = cards.groupBy { it }
        return grouped.filter {it.value.size == 3}. count() == 1 && grouped.filter { it.value.size == 2 }.count() == 1
    }
}

data object FourOfAKind: HandType {
    override fun isHandType(cards: List<Card>): Boolean {
        return cards.groupBy { it }.filter {it.value.size == 4}. count() == 1
    }
}

data object FiveOfAKind: HandType {
    override fun isHandType(cards: List<Card>): Boolean {
        return cards.groupBy { it }.filter {it.value.size == 5}. count() == 1
    }
}

val ALL_CARDS: List<Card> = listOf("2", "3" , "4", "5", "6", "7", "8", "9", "10", "T", "J", "Q", "K", "A")
val HAND_TYPES = listOf(HighCard, OnePair, TwoPairs, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind)

fun main() {
    val inputLines = readInputFileLinesTrimmed("year2023/day07/input.data")

    val cards = parseHands(inputLines)
    println("Cards")
    cards.printIt()

    val cardsToHandTypes = cards.associateWith { it.highestHand }
    println("Cards to Hand Types")
    cardsToHandTypes.printIt()

    val cardsSorted = cardsToHandTypes.toSortedMap()
    println("Cards sorted")
    cardsSorted.printIt()

    val mappedByWinnings = cardsSorted.keys.reversed().mapIndexed { index, hand -> hand to (index + 1) * hand.bid }
    println("Mapped by winnings")
    mappedByWinnings.printIt()

    val totalWinnings = mappedByWinnings.map { it.second }.sum()
    println("Total winnings: $totalWinnings") // 250118504 too low
}

fun Card.getCardValue(): Int {
    return ALL_CARDS.indexOf(this)
}

fun parseHands(lines: List<String>) = lines.map { parseHand(it) }

fun parseHand(hand: String): Hand {
    val cardsBid = hand.split(" ")
    val cards = cardsBid[0]
    val bid = cardsBid[1].toLong()
    return Hand(cards.map { it.toString() }, bid)
}
