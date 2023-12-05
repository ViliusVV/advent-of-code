package day03.part01

import utils.readInputFileLinesTrimmed

class Grid2D<T>(private val width: Int = 0, private val height: Int = 0, defaultValue: T = null as T) {
    private val grid = MutableList(height) { MutableList(width) { defaultValue } }
    val indices: IntRange get() = grid.indices

    operator fun get(y: Int): List<T> {
        return grid[y]
    }

    operator fun get(x: Int, y: Int): T {
        return grid[y][x]
    }

    operator fun set(x: Int, y: Int, value: T) {
        grid[y][x] = value
    }

    operator fun set(y: Int, value: Collection<T>) {
        grid[y] = value.toMutableList()
    }

    fun outOfBounds(x: Int, y: Int): Boolean {
        return x < 0 || y < 0 || x >= width || y >= height
    }

    fun load(lines: List<String>) {
        for(y in lines.indices) {
            val line = lines[y]
            for(x in line.indices) {
                this[x, y] = line[x] as T
            }
        }
    }

    override fun toString(): String {
        var str = ""
        for(y in this.indices) {
            str += "Y${y} -> "
            for(x in this[y].indices) {
                str += this[x, y]
            }
            str += "\n"
        }

        return str
    }

    companion object {
        fun fromLines(lines: List<String>): Grid2D<Char> {
            val grid = Grid2D<Char>(lines[0].length, lines.size)
            grid.load(lines)
            return grid
        }
    }
}

typealias CharGrid = Grid2D<Char>

data class NumberResult (
    val number: String,
    val start: Int,
    val end: Int
)

fun main() {
    val lines = readInputFileLinesTrimmed("year2023/day03/input.data")
    val grid = CharGrid.fromLines(lines)

    val partNums = findPartNums(grid)
    val partNumSum = partNums.sum()
    println("Part num sum: $partNumSum")
}

fun findPartNums(grid: CharGrid): List<Int> {
    val nums = mutableListOf<Int>()

    for(y in grid.indices) {
        var startX = 0
        val rowNums = mutableListOf<Int>()
        do {
            val numResult = grid.findNextNum(y, startX)
            if(numResult != null) {
                startX = numResult.end

                if(grid.boxCollidesWithSymbol(y, numResult.start, numResult.end - 1)) {
                    rowNums += numResult.number.toInt()
                }
            }
        } while (numResult != null && numResult.end != grid[y].size - 1)

        nums.addAll(rowNums)

        println("Y$y -> nums: $rowNums" )
    }

    return nums
}

private fun CharGrid.findNextNum(y: Int, startX: Int): NumberResult? {
    val row = this[y]
    var number = ""
    for (x in (startX..<row.size)) {
        val char = this[x, y]

        if(char.isDigit()) {
            number += char

            if(x == row.size - 1) {
                return NumberResult(number, x - number.length, x)
            }
        } else {
            if(number.isBlank()) {
                continue
            }

            return NumberResult(number, x - number.length, x)
        }
    }

    return null
}

private fun CharGrid.boxCollidesWithSymbol(yMid: Int, startX: Int, endX: Int): Boolean {
    for(y in (yMid - 1..yMid + 1)) {
        for (x in (startX - 1..endX + 1)) {
            if(isSymbol(x, y)) {
                return true
            }
        }
    }

    return false
}

fun CharGrid.isSymbol(x: Int, y: Int): Boolean {
    if(this.outOfBounds(x, y)) {
        return false
    }

    val char = this[x, y]

    return !(char.isDigit() || char == '.')
}