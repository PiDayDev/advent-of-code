package y24

import kotlin.math.absoluteValue

private const val DAY = "04"

fun main() {

    fun part1(rows: List<String>): Int {
        val size = rows.size
        val range = -2 * size..2 * size // way larger than the minimum

        val columns = range.map { extractString(rows) { x, _ -> x == it } }
        val diagDown = range.map { extractString(rows) { x, y -> x - y == it } }
        val diagUp = range.map { extractString(rows) { x, y -> x + y == it } }

        fun String.countXmas() = windowed(4).count { it == "XMAS" || it.reversed() == "XMAS" }

        return (rows + columns + diagDown + diagUp).sumOf { it.countXmas() }
    }


    fun part2(rows: List<String>): Int {
        val crosses = rows.flatMapIndexed { yGoal, row ->
            row.mapIndexed { xGoal, _ ->
                extractString(rows) { x, y ->
                    val dx = x - xGoal
                    val dy = y - yGoal
                    dx == 0 && dy == 0 || dx.absoluteValue == 1 && dy.absoluteValue == 1
                }
            }
        }
        val serializedCrosses = setOf("MMASS", "MSAMS", "SMASM", "SSAMM")
        return crosses.count { it in serializedCrosses }
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

private fun extractString(rows: List<String>, criterion: (x: Int, y: Int) -> Boolean): String = rows
    .mapIndexed { y, row -> row.filterIndexed { x, _ -> criterion(x, y) } }
    .joinToString("")
