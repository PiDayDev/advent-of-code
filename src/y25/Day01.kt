package y25

import kotlin.math.absoluteValue
import kotlin.math.sign

private const val DAY = "01"

fun main() {

    fun normalize(sum: Int) = (sum % 100 + 100) % 100

    fun part1(start: Int, rotations: List<Int>): Int = rotations
        .asSequence()
        .runningFold(start) { acc, i -> normalize(acc + i) }
        .count { it == 0 }

    fun part2(start: Int, rotations: List<Int>): Int = rotations
        .asSequence()
        .flatMap { number -> List(number.absoluteValue) { number.sign }.asSequence() }
        .runningFold(start) { acc, i -> normalize(acc + i) }
        .count { it == 0 }

    fun parse(input: List<String>): List<Int> = input
        .map { (if (it.startsWith("R")) +1 else -1) * it.drop(1).toInt() }

    val input = readInput("Day$DAY")
    val rotations = parse(input)

    println(part1(50, rotations))
    println(part2(50, rotations))
}

