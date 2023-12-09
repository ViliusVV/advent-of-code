package year2023.day03.part02

import utils.readInputFile
import utils.stringify

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

    fun checkAt(x: Int, y: Int, check: (T) -> Boolean): Boolean {
        if(outOfBounds(x, y)) {
            return false
        }

        return check(this[x, y])
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

data class Coord(val x: Int, val y: Int)

typealias CharGrid = Grid2D<Char>

fun main() {
    val lines = readInputFile()
    val grid = CharGrid.fromLines(lines)

    println("Grid:\n$grid")

    val gearMap = findGears(grid)
    println("Gears: ${gearMap.stringify()}")

    val gearMapFiltered = gearMap.filter { it.value.size == 2 }
    val gearRatios = gearMapFiltered.mapValues { it.value[0] * it.value[1] }
    println("Gear ratios: ${gearRatios.stringify()}")
    val gearRatioSum = gearRatios.values.sum()
    return println("Gear ratio sum: $gearRatioSum")
}

fun findGears(grid: CharGrid): Map<Coord, List<Int>> {
    val gearAdjMap = mutableMapOf<Coord, MutableList<Int>>()
    val gearRatios = mutableListOf<Int>()

    for(y in grid.indices) {
        var startX = 0
        do {
            val numResult = grid.findNextNum(y, startX)
            if(numResult != null) {
                startX = numResult.first + numResult.second.length + 1

                val gearCoord = grid.getGearCollisionCoord(y, numResult.first, startX - 2)
                if(gearCoord != null) {
                    gearAdjMap.getOrPut(gearCoord) { mutableListOf() } += numResult.second.toInt()
                }

            }
        } while (numResult != null)

    }

    return gearAdjMap
}

private fun CharGrid.findNextNum(y: Int, startX: Int): Pair<Int, String>? {
    val row = this[y]
    var startsAt = -1
    var number = ""
    for (x in (startX..<row.size)) {
        val char = this[x, y]

        if(char.isDigit()) {
            if(number.isBlank()) startsAt = x
            number += char


            if(x == row.size - 1) {
                return Pair(startsAt, number)
            }
        } else {
            if(number.isBlank()) {
                continue
            }

            return Pair(startsAt, number)
        }
    }

    return null
}

private fun CharGrid.getGearCollisionCoord(yMid: Int, startX: Int, endX: Int): Coord? {
    for(y in (yMid - 1..yMid + 1)) {
        for (x in (startX - 1..endX + 1)) {
            val isGear  = this.checkAt(x, y) { it == '*'}

            if(isGear) {
                return Coord(x, y)
            }
        }
    }

    return null
}

fun CharGrid.isGear(x: Int, y: Int): Boolean {
    if(this.outOfBounds(x, y)) {
        return false
    }

    val char = this[x, y]

    return !(char.isDigit() || char == '.')
}