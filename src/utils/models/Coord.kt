package utils.models

data class Coord(val x: Int, val y: Int) {
    fun getDelta(other: Coord): Coord {
        return Coord(other.x - x, other.y - y)
    }

    operator fun plus(other: Coord): Coord {
        return Coord(x + other.x, y + other.y)
    }

    operator fun minus(other: Coord): Coord {
        return Coord(x - other.x, y - other.y)
    }

    override fun toString(): String {
        return "(x=$x, y=$y)"
    }
}