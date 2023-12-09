package year2023.day08.part02

import utils.*
import org.apache.commons.math3.util.ArithmeticUtils

data class Instructions (
    var directions: String = "",
    var map: MutableMap<String, Pair<String, String>> = mutableMapOf()
) {
    val startingNodes by lazy { map.keys.filter { it[2] == 'A' } }
    val endingNodes by lazy {  map.keys.filter { it[2] == 'Z' } }

    fun getDirection(currentStep: Long): Char {
        val i = (currentStep % directions.length).toInt()
        return directions[i]
    }
}

fun main() {
    val input = readInputFile()
    val instructions = parseInstructions(input)

    instructions.printIt()
    println("Starting nodes: ${instructions.startingNodes}")
    println("Ending nodes: ${instructions.endingNodes}")

    val result = doVisitSequence(instructions)
    println("Taken steps: $result")
}

fun doVisitSequence(instructions: Instructions): Long {
    val ghostLoopSizes = instructions.startingNodes.map {
        println("Ghost with starting node: $it")
        val s = calculateGhostLoopSize(instructions, it)
        println("Ghost loop size: $s")
        s
    }

    var lcm = ghostLoopSizes[0]
    for(i in 1..<ghostLoopSizes.size) {
        lcm = ArithmeticUtils.lcm(lcm, ghostLoopSizes[i])
    }

    return lcm
}

fun calculateGhostLoopSize(instructions: Instructions, startingNode: String): Long {
    var currentNode = startingNode
    var takenSteps = 0L

    while(true) {

        val nextMap = instructions.map[currentNode]!!
        val nextDir = instructions.getDirection(takenSteps)

        if(nextDir == 'L') {
//            println("Going left: $currentNode --> ${nextMap.first}")
            currentNode = nextMap.first
        } else {
//            println("Going right: $currentNode --> ${nextMap.second}")
            currentNode = nextMap.second
        }

        takenSteps++

        if(currentNode in instructions.endingNodes) {
            return takenSteps
        }
    }
}

fun parseInstructions(lines: List<String>): Instructions {
    val reg = Regex("[0-9A-Z]{3}")
    val ins = Instructions()

    ins.directions = lines[0]
    for(i in 1..<lines.size) {
        val matches = reg.findAll(lines[i]).toList()
        ins.map[matches[0].value] = Pair(matches[1].value, matches[2].value)
    }

    return ins
}

