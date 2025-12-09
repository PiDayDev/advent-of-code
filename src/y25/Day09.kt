package y25

import common.intersection
import kotlin.math.max
import kotlin.math.min

private const val DAY = "09"

private fun String.toPos(): Position {
    val (x, y) = split(",").map(String::toInt)
    return Position(x, y)
}

private data class Rectangle(val x: IntRange, val y: IntRange) {
    constructor(x1: Int, x2: Int, y1: Int, y2: Int) : this(min(x1, x2)..max(x1, x2), min(y1, y2)..max(y1, y2))
    constructor(p1: Position, p2: Position) : this(p1.x, p2.x, p1.y, p2.y)

    fun dropBorders() = Rectangle(x.first + 1 until x.last, y.first + 1 until y.last)

    fun area() = (x.last - x.first + 1).toLong() * (y.last - y.first + 1)

    infix fun overlaps(other: Rectangle): Boolean =
        (x intersection other.x).isNotEmpty() && (y intersection other.y).isNotEmpty()
}

fun main() {
    fun part1(tiles: List<Position>): Long =
        tiles.maxOf { t1 ->
            tiles.maxOf { t2 ->
                Rectangle(t1, t2).area()
            }
        }

    fun part2(tiles: List<Position>): Long {
        val sides = (tiles + tiles.first()).windowed(2).map { (a, b) -> Rectangle(a, b) }
        return tiles.maxOf { t1 ->
            tiles
                .mapNotNull { t2 ->
                    val rectangle = Rectangle(t1, t2)
                    val innerRectangle = rectangle.dropBorders()
                    when {
                        sides.none { it overlaps innerRectangle } -> rectangle.area()
                        else -> null
                    }
                }
                .maxOrNull() ?: 0L
        }
    }

    val input = readInput("Day$DAY").map(String::toPos)
    println(part1(input))
    println(part2(input))
}
