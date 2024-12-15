package y24

import java.io.File

/** Reads lines from the given input txt file. */
fun readInput(name: String) = File("src/y24", "$name.txt").readLines()

data class Position(val x: Int, val y: Int) {
    operator fun plus(p: Position) =
        Position(x + p.x, y + p.y)

    operator fun times(n: Int) =
        Position(x * n, y * n)

    fun around() =
        listOf(north, south, east, west).map { dir -> this + dir }

    override fun toString() =
        "($x,$y)"
}

val north = Position(x = 0, y = -1)
val south = Position(x = 0, y = +1)
val east = Position(x = 1, y = 0)
val west = Position(x = -1, y = 0)

fun List<String>.classifyByChar(): Map<Char, List<Position>> {
    val map = mutableMapOf<Char, MutableList<Position>>()
    forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            val list = map.getOrPut(c) { mutableListOf() }
            list += Position(x, y)
        }
    }
    return map
}


enum class Direction(val movement: Position) {
    N(north),
    S(south),
    W(west),
    E(east);

    fun turnRight() = when(this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }
}
