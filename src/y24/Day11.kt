package y24

import java.math.BigInteger

private const val DAY = "11"

fun main() {
    val initialStones = readInput("Day$DAY").toStoneStats()
    println(initialStones.blinkAndCount(25))
    println(initialStones.blinkAndCount(75))
}

private fun List<String>.toStoneStats(): StoneStats {
    val startingStones = flatMap { it.split(" ") }.map { Stone(it) }
    val groups: Map<Stone, Int> = startingStones.groupingBy { it }.eachCount()
    return StoneStats(groups.mapValues { it.value.toLong() })
}

private typealias Stone = BigInteger

private val yearStone = 2024.toBigInteger()

private fun Stone.blink(): List<Stone> = when {
    this == Stone.ZERO -> listOf(Stone.ONE)
    toString().length % 2 > 0 -> listOf(this * yearStone)
    else -> split()
}

private fun Stone.split(): List<Stone> = toString().let {
    listOf(
        Stone(it.take(it.length / 2)),
        Stone(it.takeLast(it.length / 2)),
    )
}

data class StoneStats(val quantities: Map<Stone, Long>) {
    fun blinkAndCount(times: Int): Long =
        generateSequence(this) { it.blink() }
            .drop(1)
            .take(times)
            .last()
            .quantities
            .values
            .sum()

    private fun blink(): StoneStats {
        val result = mutableMapOf<Stone, Long>()
        quantities.forEach { (stone, quantity) ->
            stone.blink().forEach {
                result[it] = (result[it] ?: 0) + quantity
            }
        }
        return StoneStats(result)
    }
}