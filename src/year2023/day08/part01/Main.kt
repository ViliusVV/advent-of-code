package year2023.day08.part01

import utils.*

data class Instructions (
    var directions: String = "",
    var map: MutableMap<String, Pair<String, String>> = mutableMapOf()
) {
    fun getDirection(currentStep: Int): Char {
        return directions[currentStep % directions.length]
    }
}

fun main() {
    val input = readInputFile()
    val instructions = parseInstructions(input)

    instructions.printIt()

    val result = doVisitSequence(instructions)
    println("Taken steps: $result")
}

fun doVisitSequence(instructions: Instructions): Int {
    var currentNode = "AAA"
    var takenSteps = 0

    while(true) {
        if(currentNode == "ZZZ") {
            return takenSteps
        }

        val nextMap = instructions.map[currentNode]!!
        val nextDir = instructions.getDirection(takenSteps)


        if(nextDir == 'L') {
            currentNode = nextMap.first
            println("Going left: $currentNode --> ${nextMap.first}")
        } else {
            currentNode = nextMap.second
            println("Going right: $currentNode --> ${nextMap.second}")
        }

        takenSteps++
    }
}

fun parseInstructions(lines: List<String>): Instructions {
    val reg = Regex("[A-Z]{3}")
    val ins = Instructions()

    ins.directions = lines[0]
    for(i in 1..<lines.size) {
        val matches = reg.findAll(lines[i]).toList()
        ins.map[matches[0].value] = Pair(matches[1].value, matches[2].value)
    }

    return ins
}

