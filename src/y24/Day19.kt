package y24

private const val DAY = "19"

fun main() {
    fun validPatterns(towels: List<String>, stripePatterns: List<String>): List<String> {
        val regex = towels
            .joinToString(prefix = "^(?:", postfix = ")+" + '$', separator = "|")
            .toRegex()
        return stripePatterns.filter {
            it.matches(regex)
        }
    }

    fun countValidArrangements(
        towels: List<String>,
        stripePattern: String,
        memoizationCache: MutableMap<String, Long> = mutableMapOf()
    ): Long {
        if (stripePattern.isEmpty()) return 1L

        val known = memoizationCache[stripePattern]
        if (known != null) return known

        val result = towels
            .filter { stripePattern.startsWith(it) }
            .sumOf { towel ->
                countValidArrangements(towels, stripePattern.drop(towel.length), memoizationCache)
            }

        return result.also { memoizationCache[stripePattern] = it }
    }


    val input = readInput("Day$DAY")
    val towels = input.first().split(", ")
    val stripePatterns = input.takeLastWhile { it.isNotBlank() }

    val viablePatterns = validPatterns(towels, stripePatterns)

    val part1 = viablePatterns.size
    println("PART 1: $part1")
    check(part1 == 287)

    val part2 = viablePatterns.sumOf { countValidArrangements(towels, it) }
    println("PART 2: $part2")
    check(part2 == 571894474468161L)
}
