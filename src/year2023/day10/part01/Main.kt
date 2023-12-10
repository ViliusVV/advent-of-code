@file:Suppress("MemberVisibilityCanBePrivate")

package year2023.day10.part01
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.terminal.Terminal
import utils.*
import utils.interfaces.Grid
import utils.models.Coord
import kotlin.math.ceil
import kotlin.math.max
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration


class Grid2D<T>(val width: Int = 0,val height: Int = 0, defaultValue: T = null as T): Grid<T> {
    private val grid = MutableList(height) { MutableList(width) { defaultValue } }
    override val indices: IntRange get() = grid.indices

    override operator fun get(y: Int): List<T> {
        return grid[y]
    }

    override operator fun get(x: Int, y: Int): T {
        return grid[y][x]
    }

    fun at(pos: Coord): T {
        return this[pos.x, pos.y]
    }

    override operator fun set(x: Int, y: Int, value: T) {
        grid[y][x] = value
    }

    override operator fun set(y: Int, value: Collection<T>) {
        grid[y] = value.toMutableList()
    }

    override fun outOfBounds(x: Int, y: Int): Boolean {
        return x < 0 || y < 0 || x >= width || y >= height
    }

    fun replaceWith(func: (T) -> T): Grid2D<T> {
        val newGrid = Grid2D<T>(width, height)
        for(y in this.indices) {
            for(x in this[y].indices) {
                newGrid[x,y] = func(this[x, y])
            }
        }

        return newGrid
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
            for(y in lines.indices) {
                val line = lines[y]
                for(x in line.indices) {
                    grid[x, y] = line[x]
                }
            }
            return grid
        }
    }
}

lateinit var term: Terminal

enum class Direction(val delta: Coord) {
    NORTH(Coord(0, -1)),
    EAST(Coord(1, 0)),
    SOUTH(Coord(0, 1)),
    WEST(Coord(-1, 0));

    fun opposite(): Direction {
        return when(this) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

fun main() {
    val inputLines = readInputFile()

    val grid2D = Grid2D.fromLines(inputLines)
    val grid2DPretty = grid2D.prettify()

    grid2DPretty.printIt()

    term = createTerminal(max(grid2D.width+1, 50), max(grid2D.height + 1, 25), 11)
    term.putGrid(grid2DPretty)

    val loop = getLoopNodePositions(grid2D)
    println("Loop:\n${loop.stringify()}")

    println("Distance: ${ceil(loop.size / 2.0).toInt()}")

    term.waitForClose()
}

fun getLoopNodePositions(grid: Grid2D<Char>): List<Coord> {
    val loopNodePositions = mutableListOf<Coord>()

    val start = findStart(grid)
    grid.changeColor(start.x, start.y, TextColor.ANSI.RED_BRIGHT)

    var lastDir: Direction? = null
    var pos = start
    do {
        var dirs = grid.at(pos).getAvailableDirs()
        if(lastDir != null) {
            dirs = dirs.filter { it != lastDir?.opposite() }
        }

        for(dir in dirs) {
            val nextPos = pos + dir.delta
            val probDirs = grid.at(nextPos).getAvailableDirs()
            if(probDirs.contains(dir.opposite())) {
                lastDir = dir
                pos = nextPos
                loopNodePositions.add(pos)
                break
            }
        }

        grid.changeColor(pos.x, pos.y, TextColor.ANSI.GREEN_BRIGHT)
    } while (grid[pos.x, pos.y] != 'S')

    loopNodePositions.add(0, loopNodePositions.removeLast())

    return loopNodePositions
}

private fun Char.getAvailableDirs(): List<Direction> {
    return when(this) {
        'L' -> listOf(Direction.NORTH, Direction.EAST)
        'J' -> listOf(Direction.NORTH, Direction.WEST)
        '7' -> listOf(Direction.SOUTH, Direction.WEST)
        'F' -> listOf(Direction.SOUTH, Direction.EAST)
        '|' -> listOf(Direction.NORTH, Direction.SOUTH)
        '-' -> listOf(Direction.EAST, Direction.WEST)
        'S' -> Direction.entries
        else -> emptyList()
    }
}

private fun Grid2D<Char>.changeColor(x: Int, y: Int, color: TextColor) {
    term.putCharAt(x, y, this[x, y].prettify(), color)
    term.deferFlush(50.milliseconds.toJavaDuration())

    if(Random.nextDouble() < 0.01) {
        Thread.sleep(10)
    }
}

fun findStart(grid: Grid2D<Char>): Coord {
    for(y in grid.indices) {
        for(x in grid[y].indices) {
            if(grid[x, y] == 'S') {
                return Coord(x, y)
            }
        }
    }

    throw Exception("No start found")
}

private fun Char.prettify(): Char {
    return when (this) {
        'L' -> '└'
        'J' -> '┘'
        '7' -> '┐'
        'F' -> '┌'
        '|' -> '│'
        '-' -> '─'
        '.' -> ' '
        'S' -> '●'
        else -> this
    }
}
fun Grid2D<Char>.prettify(): Grid2D<Char> {
    return this.replaceWith { it.prettify() }
}

