package y24

private const val DAY = "22"

fun main() {

    val pruner = (1L shl 24) - 1

    fun step(n: Long): Long = n
        .let { ((it shl 6) xor it) and pruner }
        .let { ((it shr 5) xor it) and pruner }
        .let { ((it shl 11) xor it) and pruner }

    fun generate2000NumbersFrom(n: Long) = generateSequence(n) { step(it) }.take(2001)

    fun part1(sequences: List<Sequence<Long>>): Long = sequences.sumOf { it.last() }

    fun part2(sequences: List<Sequence<Long>>): Int {
        val prices: List<List<Int>> = sequences.map { seq ->
            seq.map { (it % 10L).toInt() }.toList()
        }

        val changeSequenceToTotalValue = mutableMapOf<List<Int>, Int>()
        prices.forEach { priceSequence ->
            val changes: List<List<Int>> = priceSequence.zipWithNext { a, b -> b - a }.windowed(4)
            val alreadyFound = mutableSetOf<List<Int>>()
            changes.forEachIndexed { index, sequence ->
                if (sequence !in alreadyFound) {
                    alreadyFound += sequence
                    changeSequenceToTotalValue.merge(sequence, priceSequence[index + 4], Int::plus)
                }
            }
        }

        return changeSequenceToTotalValue.values.max()
    }

    val seeds = readInput("Day$DAY").map { it.toLong() }
    val sequences = seeds.map(::generate2000NumbersFrom)

    val p1 = part1(sequences)
    println("PART 1: $p1")
    check(p1 == 20332089158L)

    val p2 = part2(sequences)
    println("PART 2: $p2")
    check(p2 < 3932376319L)
}

