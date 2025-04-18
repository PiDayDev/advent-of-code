package y24

import java.io.File
import kotlin.math.absoluteValue

/** Reads lines from the given input txt file. */
fun readInput(name: String) = File("src/y24", "$name.txt").readLines()

data class Position(val x: Int, val y: Int) {
    operator fun plus(p: Position) =
        Position(x + p.x, y + p.y)

    operator fun minus(p: Position) =
        Position(x - p.x, y - p.y)

    operator fun plus(d: Direction) = this + d.movement

    operator fun times(n: Int) =
        Position(x * n, y * n)

    operator fun rangeTo(p: Position): Set<Position> {
        val (x1, x2) = listOf(x, p.x).sorted()
        val (y1, y2) = listOf(y, p.y).sorted()
        return (y1..y2).flatMap { y ->
            (x1..x2).map { x ->
                Position(x, y)
            }
        }.toSet()
    }

    fun around() =
        listOf(north, south, east, west).map { dir -> this + dir }

    infix fun manhattanDistance(p: Position) = (x - p.x).absoluteValue + (y - p.y).absoluteValue

    override fun toString() =
        "($x,$y)"
}

val north = Position(x = 0, y = -1)
val south = Position(x = 0, y = +1)
val east = Position(x = 1, y = 0)
val west = Position(x = -1, y = 0)

fun List<String>.classifyByChar(): Map<Char, List<Position>> {
    val map = mutableMapOf<Char, List<Position>>()
    forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            map.merge(c, listOf(Position(x, y))) { a, b -> a + b }
        }
    }
    return map
}


enum class Direction(val movement: Position) {
    N(north),
    S(south),
    W(west),
    E(east);

    fun turnRight() = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }

    fun turnLeft() = turnRight().turnRight().turnRight()

    fun opposite() = turnRight().turnRight()
}

fun <T> permutations(list: List<T>): List<List<T>> {
    if (list.isEmpty()) return emptyList()
    if (list.size == 1) return listOf(list)
    return list.flatMap { elem -> permutations(list - elem).map { it + elem } }
}
