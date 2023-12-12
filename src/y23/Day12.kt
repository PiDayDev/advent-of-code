package y23

import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

private const val DAY = "12"

private typealias Key = Pair<String, List<Int>>

private val memoizationCache = mutableMapOf<Key, BigInteger>()

fun countPossibilities(report: String, clusters: List<Int>): BigInteger {
    if (clusters.isEmpty())
        return if ('#' in report) ZERO else ONE
    else if (report.isEmpty())
        return ZERO

    val key: Key = report to clusters
    val cached = memoizationCache[key]
    if (cached != null) return cached

    val index = clusters.indexOf(clusters.maxOrNull()!!)
    val prefix = clusters.take(index)
    val suffix = clusters.drop(index + 1)
    val size = clusters[index]

    val total = report
        .windowed(size)
        .mapIndexed { k, window ->
            when {
                '.' !in window && report.getOrNull(k - 1) != '#' && report.getOrNull(k + size) != '#' -> {
                    val pre = report.take((k - 1).coerceAtLeast(0))
                    val suf = report.drop(k + size + 1)
                    countPossibilities(pre, prefix) * countPossibilities(suf, suffix)
                }

                else -> ZERO
            }
        }
        .sumOf { it }
    memoizationCache[key] = total
    return total
}

fun main() {

    fun countOptions(row: String): BigInteger {
        val (statuses, damageList) = row.split(" ")
        val damages = damageList.split(",").map { it.toInt() }
        return countPossibilities(statuses, damages)
    }

    fun part1(input: List<String>) = input.sumOf(::countOptions)

    fun part2(input: List<String>): BigInteger {
        val unfolded = input
            .map { it.split(" ") }
            .map { (l, r) -> List(5) { l }.joinToString("?") + " " + List(5) { r }.joinToString(",") }
        return part1(unfolded)
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
