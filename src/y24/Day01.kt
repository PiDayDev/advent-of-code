package y24

import kotlin.math.absoluteValue

private const val DAY = "01"

fun main() {

    fun part1(left: List<Int>, right: List<Int>): Int =
        left.sorted()
            .zip(right.sorted())
            .sumOf { (a, b) -> (a - b).absoluteValue }

    fun part2(left: List<Int>, right: List<Int>): Int =
        left.sumOf { e -> right.filter { it == e }.sum() }

    fun parse(input: List<String>): Pair<List<Int>, List<Int>> = input
        .map { it.split("""\s+""".toRegex()) }
        .map { (a, b) -> a.toInt() to b.toInt() }
        .unzip()

    val input = readInput("Day$DAY")
    val (left, right) = parse(input)
    println(part1(left, right))
    println(part2(left, right))

}

