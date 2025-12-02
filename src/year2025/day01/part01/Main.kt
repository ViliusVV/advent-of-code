package year2025.day01.part01

import utils.readInputFile

const val TARGET_LOCATION = 0
const val DIAL_START = 50
const val DIAL_MAX = 99

private var location = DIAL_START
private var targetCounts = 0

fun main() {
    val lines = readInputFile()
    for (turn in lines) {
        val dir = if (turn[0] == 'R') 1 else -1
        val steps = turn.substring(1).toInt()
        val travel = dir * steps

        location = (location + travel)
        val dialLocation = (location + ((DIAL_MAX + 1) * -dir))  % (DIAL_MAX + 1)

        if(dialLocation == TARGET_LOCATION) {
            targetCounts++
        }

        println("New location $location after turn $turn")
    }

    println("✅ Answer: $targetCounts")
}