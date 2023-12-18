package y23

import y23.Direction.E
import y23.Direction.N
import y23.Direction.S
import y23.Direction.W
import java.util.*

private const val DAY = "18"

data class DigInstruction(val direction: Direction, val length: Int, val color: String)

val stringToDirection = mapOf("U" to N, "D" to S, "L" to W, "R" to E)

fun String.toDirection(): Direction = stringToDirection[this] ?: N

fun String.toDigInstruction(): DigInstruction {
    val (d, n, c) = split(" ")
    return DigInstruction(d.toDirection(), n.toInt(), c.removeSurrounding("(", ")"))
}

operator fun Position.plus(instruction: DigInstruction): List<Position> =
    (1..instruction.length).map { k ->
        this + instruction.direction.movement * k
    }

fun Set<Position>.floodFill(): Set<Position> {
    val xIndices = minOf { it.x }..maxOf { it.x }
    val yIndices = minOf { it.y }..maxOf { it.y }

    val startSearch = Position(xIndices.first - 1, (yIndices.first + yIndices.last) / 2)
    val startPosition = generateSequence(startSearch) { it + E.movement }
        .dropWhile { !contains(it) }
        .first { !contains(it) }

    val fill = mutableSetOf<Position>()

    val queue = Stack<Position>()
    queue.push(startPosition)
    while (queue.isNotEmpty()) {
        val next = queue.pop()
        if (next in this || next in fill) continue

        fill += next
        queue += next.around()
    }
    return fill
}

fun main() {
    fun digTrench(instructions: List<DigInstruction>): Set<Position> {
        var currentPosition = Position(0, 0)
        val trench = mutableSetOf(currentPosition)

        instructions.forEach { instruction ->
            val steps = currentPosition + instruction
            trench += steps
            currentPosition = steps.last()
        }
        return trench
    }

    fun part1(input: List<String>): Int {
        val instructions = input.map(String::toDigInstruction)
        val trench = digTrench(instructions)
        val fill = trench.floodFill()
        return (trench + fill).size
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = readInput("Day${DAY}_test")
        val p1 = part1(testInput)
        println("Test 1 = $p1")
        check(p1 == 62)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
