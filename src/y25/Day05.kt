package y25

import java.util.*

private const val DAY = "05"

private infix fun LongRange.union(other: LongRange): List<LongRange> {
    val bFirst = other.first
    val bLast = other.last
    return when {
        bFirst > last -> listOf(this, other)
        bLast < first -> listOf(this, other)
        bFirst in this && bLast in this -> listOf(this)
        first in other && last in other -> listOf(other)
        bFirst in this -> listOf(first..bLast)
        bLast in this -> listOf(bFirst..last)
        else -> emptyList()
    }.sortedBy { it.first }
}

private fun LongRange.size() = last - first + 1L

fun main() {
    fun part1(ranges: List<LongRange>, ids: List<Long>) = ids.count { id ->
        ranges.any { range -> id in range }
    }

    fun part2(ranges: List<LongRange>): Long {
        val stack = Stack<LongRange>()
        ranges
            .sortedBy { it.first }
            .forEach { range ->
                when {
                    stack.isEmpty() -> stack.push(range)
                    else -> (stack.pop() union range).forEach { stack.push(it) }
                }
            }
        return stack.sumOf { it.size() }
    }

    val input = readInput("Day$DAY")
    val ranges = input
        .takeWhile { it.isNotBlank() }
        .map { it.split("-") }
        .map { (a, b) -> a.toLong()..b.toLong() }
    val ids = input
        .takeLastWhile { it.isNotBlank() }
        .map { it.toLong() }

    println(part1(ranges, ids))
    val p2 = part2(ranges)
    check(p2 < 721937157378027)
    println(p2)
}
