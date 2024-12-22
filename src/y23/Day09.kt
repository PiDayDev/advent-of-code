package y23

private const val DAY = "09"

fun main() {
    fun extrapolateNext(numbers: List<Long>): Long {
        if (numbers.all { it == 0L }) return 0

        val differences = numbers.zipWithNext { a, b -> b - a }
        return numbers.last() + extrapolateNext(differences)
    }

    fun part1(input: List<List<Long>>) =
        input.sumOf { extrapolateNext(it) }

    fun part2(input: List<List<Long>>) =
        part1(input.map { it.reversed() })

    val input = readInput("Day$DAY").map { it.toLongs() }
    println(part1(input))
    println(part2(input))
}

private fun String.toLongs() = split(" ").map { it.toLong() }
