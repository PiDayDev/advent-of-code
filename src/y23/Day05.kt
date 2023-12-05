package y23

private const val DAY = "05"

fun main() {
    fun List<String>.seeds() =
        first().substringAfter(": ").split(" ").map { it.toLong() }

    fun part1(input: List<String>): Long {
        val seeds = input.seeds()
        val sections = input.drop(2).sections()

        var result = seeds
        sections.forEach { section ->
            val mapper = Mapper(section)
            result = result.map { mapper.convert(it) }
        }

        return result.minOf { it }
    }

    fun part2(input: List<String>): Long {
        val sections = input.drop(2).sections()
        val mappers = sections.map { Mapper(it) }

        val seedsRanges: List<LongRange> = input.seeds()
            .chunked(2)
            .map { (a, b) -> a until a + b }

        val convertedRanges: List<LongRange> = mappers
            .fold(seedsRanges) { ranges, mapper ->
                ranges.flatMap { mapper.convertRange(it) }
            }
        return convertedRanges.minOf { it.first }
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

fun List<String>.sections(): List<List<String>> =
    fold(listOf(mutableListOf<String>())) { acc, curr ->
        if (curr.isBlank())
            acc + listOf(mutableListOf())
        else {
            acc.last().add(curr)
            acc
        }

    }

private data class Mapper(val section: List<String>) {
    private val rangeToIncrement = mutableMapOf<LongRange, Long>()

    init {
        section
            .drop(1)
            .forEach { row ->
                val (target, origin, length) = row.split(" ").map { it.toLong() }
                val originRange = origin until origin + length
                val difference = target - origin
                rangeToIncrement[originRange] = difference
            }
    }

    fun convert(n: Long): Long {
        val increment = rangeToIncrement.entries
            .firstNotNullOfOrNull { (range, diff) -> if (n in range) diff else null }
        return (increment ?: 0L) + n
    }

    fun convertRange(range: LongRange): List<LongRange> = split(range)
        .map { r -> convert(r.first)..convert(r.last) }

    private fun split(range: LongRange): List<LongRange> {
        val limits = rangeToIncrement.keys
            .map { r -> range.intersect(r) }
            .filterNot { it.isEmpty() }
            .sortedBy { it.first }

        if (limits.isEmpty())
            return listOf(range)

        val edges = listOf(
            range.first until limits.first().first,
            limits.last().last + 1..range.last,
        )
        val holes = limits.windowed(2).map { (left, right) ->
            left.last + 1 until right.first
        }

        return (limits + edges + holes).filterNot { it.isEmpty() }
    }
}

private fun LongRange.intersect(other: LongRange): LongRange {
    val bFirst = other.first
    val bLast = other.last
    return when {
        bFirst > last -> LongRange.EMPTY
        bLast < first -> LongRange.EMPTY
        bFirst in this && bLast in this -> other
        first in other && last in other -> this
        bFirst in this -> bFirst..last
        bLast in this -> first..bLast
        else -> LongRange.EMPTY
    }
}
