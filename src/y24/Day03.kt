package y24

private const val DAY = "03"

fun main() {

    fun part1(input: List<String>): Int {
        val regex = """mul\(\d+,\d+\)""".toRegex()
        return input
            .asSequence()
            .flatMap { regex.findAll(it) }
            .map { it.value }
            .sumOf { it.multiply() }
    }

    fun part2(input: List<String>): Int {
        val regex = """mul\(\d+,\d+\)|do\(\)|don't\(\)""".toRegex()
        val parts = input
            .asSequence()
            .flatMap { regex.findAll(it) }
            .map { it.value }
            .toList()
        var total = 0
        var multiplier = 1
        parts.forEach {
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

private fun String.multiply(): Int {
    val (a, b) = removePrefix("mul(").removeSuffix(")").split(",")
    return a.toInt() * b.toInt()
}
