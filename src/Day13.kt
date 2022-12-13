sealed interface PacketItem13 : Comparable<PacketItem13>

class Number13(val n: Int) : PacketItem13 {
    override operator fun compareTo(other: PacketItem13): Int =
        when (other) {
            is Number13 -> n.compareTo(other.n)
            is List13 -> List13(this).compareTo(other)
        }

    override fun toString() = "$n"
}

class List13(items: Collection<PacketItem13>) : List<PacketItem13> by ArrayList(items), PacketItem13 {
    constructor(item: Number13) : this(listOf(item))

    override operator fun compareTo(other: PacketItem13): Int =
        when (other) {
            is Number13 -> compareTo(List13(other))
            is List13 ->
                zip(other).asSequence().map { (a, b) -> a.compareTo(b) }.firstOrNull { it != 0 }
                    ?: size.compareTo(other.size)
        }

    override fun toString() = joinToString(separator = ",", prefix = "[", postfix = "]")
}

typealias Couple13 = Pair<List13, List13>

private fun Couple13.isOk() = first < second

/**
 * Didn't want to spend too much time on parsing, so I transformed
 *  txt input into a Kotlin class using IDE replacement tools.
 */
fun main() {
    fun part1(input: List<Couple13>) = input
        .mapIndexed { index, pair ->
            when {
                pair.isOk() -> index + 1
                else -> 0
            }
        }.sum()

    fun part2(input: List<Couple13>): Int {
        val divider2 = Day13Input.makeDividerPacket(2)
        val divider6 = Day13Input.makeDividerPacket(6)
        val allPackets = input.flatMap { it.toList() } + divider2 + divider6
        val sorted = allPackets.sorted()
        val idx2 = sorted.indexOfFirst { it.compareTo(divider2) == 0 }
        val idx6 = sorted.indexOfFirst { it.compareTo(divider6) == 0 }
        return (idx2 + 1) * (idx6 + 1)
    }

    val input = Day13Input.getParsedInput()
    println(part1(input))
    println(part2(input))
}
