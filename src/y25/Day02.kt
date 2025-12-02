package y25

private const val DAY = "02"

fun main() {

    infix fun LongRange.withRepetitions(repeats: Int): Set<Long> {
        val bLen = "$first".length
        val eLen = "$last".length
        if (bLen == eLen) {
            val startHasExactLength = bLen % repeats == 0
            return when {
                startHasExactLength -> {
                    val prefix1 = "$first".take(bLen / repeats)
                    val prefix2 = "$last".take(bLen / repeats)
                    (prefix1.toLong()..prefix2.toLong())
                        .map { "$it".repeat(repeats).toLong() }
                        .filter { contains(it) }
                        .toSet()
                }

                else -> emptySet()
            }
        }

        val newBegin = """1${"0".repeat(bLen)}""".toLong()
        return (first until newBegin).withRepetitions(repeats) +
                (newBegin..last).withRepetitions(repeats)
    }

    fun part1(ranges: List<LongRange>): Long = ranges
        .flatMap { it withRepetitions 2 }
        .sumOf { it }

    fun part2(ranges: List<LongRange>): Long = ranges
        .flatMap { listOf(2, 3, 5, 7).flatMap { n -> it withRepetitions n } }
        .toSet()
        .sumOf { it }

    fun parse(input: String): List<LongRange> = input
        .split(",")
        .map {
            val (l, r) = it.split("-")
            l.toLong()..r.toLong()
        }

    val input = readInput("Day$DAY")
    val ranges = parse(input.joinToString("").trim())

    println(part1(ranges))
    println(part2(ranges))
}

