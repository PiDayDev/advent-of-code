package y16

import union

private const val day = "20"

private const val MAX = 4_294_967_295L

fun main() {

    fun validIps(input: List<String>): List<Long> {
        val ranges: List<ClosedRange<Long>> = input
            .map { range ->
                val (lo, hi) = range.split("-").map { it.toLong() }
                lo..hi
            }
            .sortedBy { it.first }

        val merged = generateSequence(ranges) { it.union() }
            .zipWithNext()
            .first { it.second.size == it.first.size }
            .second
            .sortedBy { it.start }

        val complement = merged
            .mapIndexedNotNull { index, range ->
                if (range.endInclusive >= MAX) null
                else range.endInclusive + 1 until merged[index + 1].start
            }

        return complement.flatMap { it.toList() }
    }

    val input = readInput("Day${day}")

    val ips = validIps(input)
    println(ips.first())
    println(ips.size)
}
