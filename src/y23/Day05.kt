package y23

private const val DAY = "05"

fun main() {
    fun List<String>.longs() =
        first().substringAfter(": ").split(" ").map { it.toLong() }

    fun part1(input: List<String>): Long {
        val seeds = input.longs()
        val sections = input.drop(2).sections()

        var result = seeds
        sections.forEach { section ->
            val mapper = Mapper(section)
            result = result.map { mapper.map(it) }
        }

        return result.minOf { it }
    }

    /**
     * ⚠ Warning: terrible performances
     */
    fun part2(input: List<String>): Long {
        val seeds: Sequence<Long> = input.longs()
            .chunked(2)
            .map { (a,b)-> (a until a+b).asSequence() }
            .asSequence()
            .flatten()
        val sections = input.drop(2).sections()
        val mappers = sections.map { Mapper(it) }

        val (m1,m2,m3,m4,m5) = mappers
        val (m6,m7) = mappers.drop(5)

        val min=seeds
            .map { m1.map(it) }
            .map { m2.map(it) }
            .map { m3.map(it) }
            .map { m4.map(it) }
            .map { m5.map(it) }
            .map { m6.map(it) }
            .map { m7.map(it) }
            .minOf { it }
        return min
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

data class Mapper(val section: List<String>) {
    val id = section.first()
    private val rangeToIncrement = mutableMapOf<LongRange, Long>()

    init {
        val mappings = section.drop(1)
        mappings.forEach { row ->
            val (target, origin, length) = row.split(" ").map { it.toLong() }
            val originRange = origin until origin + length
            val difference = target - origin
            rangeToIncrement[originRange] = difference
        }
    }

    fun map(n: Long): Long {
        val increment = rangeToIncrement.entries
            .firstNotNullOfOrNull { (range, diff) -> if (n in range) diff else null }
        return (increment ?: 0L) + n
    }
}
