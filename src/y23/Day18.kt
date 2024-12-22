package y23

import y23.Direction.*
import kotlin.math.absoluteValue
import kotlin.math.sign

private const val DAY = "18"

data class DigInstruction(val direction: Direction, val length: Int)

val stringToDirection = mapOf("U" to N, "D" to S, "L" to W, "R" to E)

fun String.toDigInstruction(): DigInstruction =
    split(" ").let { (d, n) -> DigInstruction(stringToDirection[d] ?: N, n.toInt()) }

val idToDirection = mapOf('0' to E, '1' to S, '2' to W, '3' to N)

fun String.toDigInstructionPart2(): DigInstruction {
    val encoded = substringAfter("(#").substringBefore(")")
    val direction = idToDirection[encoded.last()] ?: N
    val length = encoded.take(5).toInt(16)
    return DigInstruction(direction, length)
}

fun area(instructions: List<DigInstruction>): Long {
    val curvatures = (instructions.takeLast(1) + instructions + instructions.take(1))
        .windowed(3)
        .map { (a, b, c) -> (a.direction to b.direction).turn() + (b.direction to c.direction).turn() }
        .map { it.sign }
    val globalCurvature = curvatures.sum().sign

    val positions = mutableListOf(Position(0, 0))
    instructions.zip(curvatures).forEach { (dig, curvature) ->
        val convexityAdjustment = curvature * globalCurvature
        val move = dig.direction.movement * (dig.length + convexityAdjustment)
        positions.add(positions.last() + move)
    }

    return positions.shoelaceArea()
}

private fun List<Position>.shoelaceArea() = windowed(2)
    .sumOf { (a, b) -> a.x.toLong() * b.y.toLong() - a.y.toLong() * b.x.toLong() }
    .absoluteValue / 2

fun main() {
    fun part1(input: List<String>) = area(input.map(String::toDigInstruction))
    fun part2(input: List<String>) = area(input.map(String::toDigInstructionPart2))

    val input = readInput("Day$DAY")
    println("PART 1 [SHOELACE] = ${part1(input)}")
    println("PART 2 [SHOELACE] = ${part2(input)}")
}

/* 1 = clockwise, -1 = CCW */
fun Pair<Direction, Direction>.turn(): Int {
    val (a, b) = this
    return when (a) {
        N -> if (b == E) +1 else if (b == W) -1 else 0
        S -> if (b == E) -1 else if (b == W) +1 else 0
        W -> if (b == S) -1 else if (b == N) +1 else 0
        E -> if (b == S) +1 else if (b == N) -1 else 0
    }
}
