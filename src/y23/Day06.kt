package y23

import kotlin.math.ceil
import kotlin.math.sqrt

private const val DAY = "06"

/**
Inequality:
(T-x) * x >= D
where
- T = time allotted
- D = current best distance
- x = button pressing time = speed

In normal form:
x² - T * x + D<=0

Solutions for associated equation
x₁,₂ = (-b ± √Δ) / (2a)
where
-b = -(-T) = T
Δ = b²-4ac = T² - 4 * 1 * D = T²-4D
2a = 2 * 1 = 2

So
x₁,₂ = (T ± √(T²-4D)) / 2
 */

fun main() {
    fun waysToBeat(t: Long, d: Long): Long {
        val delta = (t * t - 4 * d).toDouble()
        val x1 = (t - sqrt(delta)) / 2
        val x2 = (t + sqrt(delta)) / 2
        return (ceil(x2) - ceil(x1)).toLong()
    }

    fun part1(input: List<String>): Long {
        fun String.integers() =
            substringAfter(":").trim().split("""\s+""".toRegex()).map { it.toInt() }

        val times = input.first().integers()
        val distances = input.last().integers()

        val ways = times.indices.map { j ->
            val t = times[j]
            val d = distances[j]
            waysToBeat(t.toLong(), d.toLong())
        }
        return ways.reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Long {
        fun String.integer() =
            substringAfter(":").replace(" ", "").toLong()

        val t = input.first().integer()
        val d = input.last().integer()
        return waysToBeat(t, d)
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

