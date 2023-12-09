package year2023.day09.part01

import utils.*


fun main() {
    val inputLines = readInputFile()

    val histories = parseHistories(inputLines)
    println("Histories")
    histories.printIt()

    val genSeqs =  histories.map { getSequenceNextValue(sequenceUntilZeroes(it)) }

    val sum = genSeqs.sumOf { it.first().last() }
    println("Sum: $sum")
}

fun getSequenceNextValue(seqs: List<List<Long>>): List<List<Long>> {
    var copySeqs = seqs.map { it.toMutableList() }.reversed()
    copySeqs[0] += 0

    for(i in 1..<copySeqs.size) {
        val newEl = copySeqs[i-1].last() + copySeqs[i].last()
        copySeqs[i] += newEl
    }
    copySeqs = copySeqs.reversed()

    println("New sequences")
    printSequences(copySeqs)

    return copySeqs
}

fun sequenceUntilZeroes(inputSeq: List<Long>): List<List<Long>> {
    val seq = mutableListOf<List<Long>>()
    var currentSeq = inputSeq
    while(!currentSeq.all { it == 0L }) {
        seq.add(currentSeq)
        currentSeq = generateSubsequence(currentSeq)
    }

    seq.add(currentSeq)

    println("Generated sequences")
    printSequences(seq)

    return seq
}

fun generateSubsequence(inputSeq: List<Long>): List<Long> {
    val seq = mutableListOf<Long>()
    for(i in 1..<inputSeq.size) {
        seq.add(inputSeq[i] - inputSeq[i - 1])
    }

    return seq
}

fun printSequences(seqs: List<List<Long>>) {
    for(i in seqs.indices) {
        val listStr = seqs[i].padRight(4)
        println("  ".repeat(i) + listStr)
    }
}

fun parseHistories(inputLines: List<String>): List<List<Long>> {
    return inputLines.map { it.toLongs() }
}
