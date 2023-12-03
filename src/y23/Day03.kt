package y23

private const val DAY = "03"

fun main() {
    fun part1(input: List<String>): Int {
        val symbols: Map<Int, List<Int>> = input
            .mapIndexed { y, row -> y to row.symbolsPositions("""[^.\d]""".toRegex()) }
            .toMap()

        fun anyAdjacentTo(y: Int, xs: IntRange): Boolean =
            (y - 1..y + 1).any {
                (symbols[it] ?: emptyList()).any { x ->
                    x in xs.first - 1..xs.last + 1
                }
            }

        return input
            .flatMapIndexed { y, row ->
                row.numbersPositions()
                    .filter { anyAdjacentTo(y, it) }
                    .map { row.numberAt(it) }
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val numbers: Map<Int, List<IntRange>> = input
            .mapIndexed { y, row -> y to row.numbersPositions() }
            .toMap()

        fun ratio(gearX: Int, gearY: Int): Int {
            val list = (gearY - 1..gearY + 1).flatMap { numberY ->
                (numbers[numberY] ?: emptyList())
                    .filter { gearX in it.first - 1..it.last + 1 }
                    .map { input[numberY].numberAt(it) }
            }
            return when (list.size) {
                2 -> list.first() * list.last()
                else -> 0
            }
        }

        val candidateGears = input
            .mapIndexed { y, row -> y to row.symbolsPositions("""\*""".toRegex()) }

        return candidateGears
            .sumOf { (y, xs) ->
                xs.sumOf { x -> ratio(x, y) }
            }
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

private fun String.symbolsPositions(regex: Regex): List<Int> =
    mapIndexedNotNull { index, c ->
        if (regex.matches("" + c)) index
        else null
    }

private fun String.numbersPositions(): List<IntRange> =
    """\d+""".toRegex()
        .findAll(this)
        .map { it.range }
        .toList()

private fun String.numberAt(xs: IntRange) = substring(xs).toInt()
