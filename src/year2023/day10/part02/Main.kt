@file:Suppress("MemberVisibilityCanBePrivate")

package year2023.day10.part02
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.terminal.Terminal
import utils.*
import utils.models.Coord
import year2023.day10.part01.Direction
import year2023.day10.part01.Grid2D
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

lateinit var term: Terminal

fun main() {
    val inputLines = readInputFile()

    val grid2D = Grid2D.fromLines(inputLines)
    val grid2DPretty = grid2D.prettify()

    grid2DPretty.printIt()

    term = createTerminal(max(grid2D.width+1, 50), max(grid2D.height + 1, 25), 30)
    term.putGrid(grid2DPretty)

    val loop = getLoopNodePositions(grid2D)
    println("Loop:\n${loop.stringify()}")

    val boundaryElements = scanThroughGrid(grid2D, loop)
    println("Boundary elements:\n${boundaryElements.stringify()}")
    println("Boundary elements count: ${boundaryElements.size}")


    grid2D.replaceStartWithPipe(loop)
    grid2D.colorInsideBoundary(boundaryElements)



    term.waitForClose()
}

fun Grid2D<Char>.colorInsideBoundary(boundaryEls: List<Coord>) {
    for(el in boundaryEls) {
        this.changeColor(el.x, el.y, TextColor.ANSI.BLUE_BRIGHT, TextColor.ANSI.WHITE)
    }
}

fun scanThroughGrid(grid: Grid2D<Char>, loop: List<Coord>): List<Coord> {
    val boundaryElements = mutableListOf<Coord>()

    for(y in grid.indices) {
        boundaryElements += grid.scanThroughLine(grid[y], loop, y)
    }

    return boundaryElements
}

fun Grid2D<Char>.scanThroughLine(line: List<Char>, loop: List<Coord>, y:Int): List<Coord> {
    val boundaryElements = mutableListOf<Coord>()
    var wallCount = 0
    var x = 0

    fun eatUpPipes() {
        this.changeColor(x, y, TextColor.ANSI.RED_BRIGHT)
        val startingPipeDir = line[x++].getAvailableDirs().filter { it.isUpOrDown() }

        while (loop.contains(Coord(x, y)) && line[x].isHorizontalPip()) {
            this.changeColor(x, y, TextColor.ANSI.YELLOW_BRIGHT)
            x++
        }

        this.changeColor(x-1, y, TextColor.ANSI.CYAN_BRIGHT)
        val endPipe = line[x-1].getAvailableDirs().filter { it.isUpOrDown() }

        val addWalls = startingPipeDir.intersect(endPipe.toSet()).isEmpty()
        wallCount += if(addWalls) 1 else 2

    }


    while(x < line.size - 1) {
        if(wallCount % 2 == 1 && !loop.contains(Coord(x, y))) {
            boundaryElements += Coord(x, y)
        }

        if(loop.contains(Coord(x, y)) && loop.contains(Coord(x+1, y))) {
            eatUpPipes()
        } else if(loop.contains(Coord(x, y) ) && !loop.contains(Coord(x+1, y))) {
            this.changeColor(x, y, TextColor.ANSI.RED_BRIGHT)
            wallCount++
            x++
        } else {
            x++
        }
    }

    return boundaryElements
}

fun Grid2D<Char>.replaceStartWithPipe(loop: List<Coord>): Char? {
    val start = findStart(this)
    val first = loop[1]
    val last = loop.last()

    val requiredDir1 = (start - first)
    val requiredDir2 = (start - last)

    val dir1 = Direction.entries.find { it.delta == requiredDir1 }
    val dir2 = Direction.entries.find { it.delta == requiredDir2 }

    val pipe = "FJL7|-".find { it.getAvailableDirs().containsAll(listOf(dir1, dir2)) }

    return pipe
}

fun Char?.isPipe(): Boolean {
    return this.getAvailableDirs().isNotEmpty()
}

fun Char?.isVerticalPip(): Boolean {
    return this.getAvailableDirs().any{ it == Direction.NORTH || it == Direction.SOUTH }
}

fun Char?.isHorizontalPip(): Boolean {
    return this == '-' || this == 'S'
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

        grid.changeColor(pos.x, pos.y, TextColor.ANSI.GREEN_BRIGHT, noDelay=true)
    } while (grid[pos.x, pos.y] != 'S')

    loopNodePositions.add(0, loopNodePositions.removeLast())

    return loopNodePositions
}

private fun Char?.getAvailableDirs(): List<Direction> {
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

private fun Grid2D<Char>.changeColor(x: Int, y: Int, color: TextColor, bgColor: TextColor? = null, noDelay: Boolean = false) {
    if(this.outOfBounds(x, y)) return

    term.putCharAt(x, y, this[x, y].prettify(), color, bgColor)
    term.deferFlush(50.milliseconds.toJavaDuration())

    if(noDelay) return
    Thread.sleep(1)

//    if(Random.nextDouble() < 0.01) {
//        Thread.sleep(10)
//    }
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
        '.' -> '.'
        'S' -> '●'
        else -> this
    }
}
fun Grid2D<Char>.prettify(): Grid2D<Char> {
    return this.replaceWith { it.prettify() }
}

