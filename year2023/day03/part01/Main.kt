package day03.part01

import utils.readInputFileLinesTrimmed

typealias Grid = MutableList<List<Char>>

data class NumberResult (
    val number: String,
    val start: Int,
    val end: Int
)

fun main() {
    val lines = readInputFileLinesTrimmed("year2023/day03/input.data")

    val grid = toGrid(lines);
    grid.print()

    val partNums = findPartNums(grid)
    val partNumSum = partNums.sum()
    println("Part num sum: $partNumSum")
}

fun findPartNums(grid: Grid): List<Int> {
    val nums = mutableListOf<Int>()

    for(rowIdx in grid.indices) {
        var startIdx = 0
        val rowNums = mutableListOf<Int>()
        do {
            val numResult = grid.findNextNum(rowIdx, startIdx)
            if(numResult != null) {
                startIdx = numResult.end

                if(grid.boxCollidesWithSymbol(rowIdx, numResult.start, numResult.end - 1)) {
                    rowNums += numResult.number.toInt()
                }
            }
        } while (numResult != null && numResult.end != grid[rowIdx].size - 1)

        nums.addAll(rowNums)

        println("Row $rowIdx -> nums: ${rowNums}" )
    }

    return nums
}

fun Grid.findNextNum(rowIdx: Int, start: Int): NumberResult? {
    val row = this[rowIdx]
    var number = ""
    for (idx in (start..<row.size)) {
        val char = row[idx]

        if(char.isDigit()) {
            number += char
            if(idx == row.size - 1) {
                return NumberResult(number, idx - number.length, idx)
            }

        } else {
            if(number.isBlank()) {
                continue
            }

            return NumberResult(number, idx - number.length, idx)
        }
    }

    return null
}

fun Grid.boxCollidesWithSymbol(rowIdx: Int, start: Int, end: Int): Boolean {
    for(rIdx in (rowIdx-1..rowIdx+1)) {
        for (idx in (start-1..end+1)) {
            if(isSymbol(rIdx, idx)) {
                return true
            }
        }
    }

    return false
}

fun Grid.isSymbol(rowIdx: Int, idx: Int): Boolean {
    if (rowIdx < 0 || idx < 0) return false
    if(this.size <= rowIdx) return false
    if(this[rowIdx].size <= idx) return false

    val char = this[rowIdx][idx]

    return !(char.isDigit() || char == '.')
}

fun Grid.print() {
    var str = ""
    for(i in this.indices) {
        str += "Row ${i} -> "
        for(j in this[i].indices) {
            str += this[i][j]
        }
        str += "\n"
    }

   print(str)
}

fun toGrid(lines: List<String>): Grid {
    val grid = MutableList(lines.size){List(0) {'.'}}

    for(i in lines.indices) {
        grid[i] = lines[i].toCharArray().asList()
    }

    return grid
}
