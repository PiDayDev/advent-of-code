package y23

import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/y23", "$name.txt").readLines()

data class Position(val x: Int, val y: Int) {
    operator fun plus(p: Position) =
        Position(x + p.x, y + p.y)

    fun around() =
        listOf(north, south, east, west).map { dir -> this + dir }

    override fun toString() =
        "($x,$y)"
}

val north = Position(x = 0, y = -1)
val south = Position(x = 0, y = +1)
val east = Position(x = 1, y = 0)
val west = Position(x = -1, y = 0)

enum class Direction(val movement: Position) {
    N(north),
    S(south),
    W(west),
    E(east)
}
