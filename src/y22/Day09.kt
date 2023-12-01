package y22

import kotlin.math.sign

private const val DAY = "09"

private data class Knot(val x: Int = 0, val y: Int = 0) {

    fun move(direction: Direction) = Knot(x + direction.dx, y + direction.dy)

    fun follow(head: Knot): Knot {
        val dx = head.x - x
        val dy = head.y - y
        return when {
            dx in -1..1 && dy in -1..1 -> this
            else -> Knot(x + dx.sign, y + dy.sign)
        }
    }
}

fun main() {
    fun part1(movements: List<Direction>): Int {
        var head = Knot()
        var tail = Knot()
        val visited = mutableSetOf(tail)
        movements.forEach { dir ->
            head = head.move(dir)
            tail = tail.follow(head)
            visited += tail
        }
        return visited.size
    }

    fun part2(movements: List<Direction>): Int {
        var head = Knot()
        val tails = MutableList(9) { Knot() }
        val visited = mutableSetOf(head)
        movements.forEach { dir ->
            head = head.move(dir)
            tails[0] = tails[0].follow(head)
            (1..8).forEach {
                tails[it] = tails[it].follow(tails[it - 1])
            }
            visited += tails.last()
        }
        return visited.size
    }

    val input = readInput("Day$DAY").toMovements()
    println(part1(input))
    println(part2(input))
}

private fun List<String>.toMovements() = map { it.split(" ") }
    .flatMap { (direction, distance) ->
        List(distance.toInt()) { direction.toDirection() }
    }

private fun String.toDirection() = Direction.values().first { it.toString().startsWith(this) }
