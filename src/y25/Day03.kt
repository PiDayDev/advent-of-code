package y25

private const val DAY = "03"

fun main() {

    fun String.highestSubNumberOfLength(digits: Int): Long {
        if (digits == 1)
            return max().digitToInt().toLong()

        val next = digits - 1
        val large = dropLast(next).max()
        val skip = indexOf(large) + 1
        val small = drop(skip).highestSubNumberOfLength(next)
        return "$large$small".toLong()
    }

    fun part1(input: List<String>) =
        input.sumOf { it.highestSubNumberOfLength(2) }

    fun part2(input: List<String>) =
        input.sumOf { it.highestSubNumberOfLength(12) }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
