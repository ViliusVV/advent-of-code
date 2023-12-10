package utils.interfaces

interface Grid<T> {
    val indices: IntRange
    operator fun get(y: Int): List<T>
    operator fun get(x: Int, y: Int): T

    operator fun set(x: Int, y: Int, value: T)
    operator fun set(y: Int, value: Collection<T>)

    fun outOfBounds(x: Int, y: Int): Boolean
}