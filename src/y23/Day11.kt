package y23

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

private const val DAY = "11"

fun main() {
    val input = readInput("Day$DAY")

    fun manhattan(a: Position, b: Position) = (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue

    fun range(a: Int, b: Int) = min(a, b)..max(a, b)

    fun totalDistance(input: List<String>, expansionRate: Long): Long {
        val (galaxies, expandingRows, expandingCols) = parse(input)
        var total = 0L
        for (i in galaxies.indices) {
            val first = galaxies[i]
            for (j in i + 1..galaxies.lastIndex) {
                val second = galaxies[j]
                val baseDistance = manhattan(first, second)
                val expansionCount = expandingCols.count { it in range(first.x, second.x) } +
                        expandingRows.count { it in range(first.y, second.y) }
                val distance = baseDistance + expansionCount * (expansionRate - 1)
                total += distance
            }
        }
        return total
    }

    fun part1(input: List<String>) = totalDistance(input, 2)

    fun part2(input: List<String>) = totalDistance(input, 1_000_000L)

    println(part1(input))
    println(part2(input))
}

private fun parse(input: List<String>): Triple<List<Position>, List<Int>, List<Int>> {
    val galaxies = mutableListOf<Position>()
    val expandingRows = mutableListOf<Int>()
    val expandingCols = mutableListOf<Int>()

    input.forEachIndexed { y, row ->
        if (row.all { it == '.' })
            expandingRows += y

        row.forEachIndexed { x, c ->
            if (c == '#')
                galaxies += Position(x, y)
        }
    }
    input.first().indices.forEach { x ->
        if (input.all { it[x] == '.' })
            expandingCols += x
    }
    return Triple(galaxies, expandingRows, expandingCols)
}
