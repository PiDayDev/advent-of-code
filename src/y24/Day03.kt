package y24

private const val DAY = "03"

fun main() {

    fun part1(input: List<String>): Int = input
        .extractTokens("""mul\(\d+,\d+\)""".toRegex())
        .sumOf { it.multiply() }

    fun part2(input: List<String>): Int {
        var total = 0
        var multiplier = 1
        input
            .extractTokens("""mul\(\d+,\d+\)|do\(\)|don't\(\)""".toRegex())
            .forEach {
                when (it) {
                    "do()" -> multiplier = 1
                    "don't()" -> multiplier = 0
                    else -> total += multiplier * it.multiply()
                }
            }
        return total
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

fun List<String>.extractTokens(regex: Regex) = asSequence()
    .flatMap { regex.findAll(it) }
    .map { it.value }
    .toList()

private fun String.multiply(): Int {
    val (a, b) = removePrefix("mul(").removeSuffix(")").split(",")
    return a.toInt() * b.toInt()
}
