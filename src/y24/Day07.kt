package y24

private const val DAY = "07"

fun main() {
    val sum: (Long, Long) -> Long = { a, b -> a + b }
    val product: (Long, Long) -> Long = { a, b -> a * b }
    val concat: (Long, Long) -> Long = { a, b -> "$a$b".toLong() }

    fun part1(input: List<Equation>) = input
        .filter { it.isSolvable(setOf(sum, product)) }
        .sumOf { it.expected }

    fun part2(input: List<Equation>) = input
        .filter { it.isSolvable(setOf(sum, product, concat)) }
        .sumOf { it.expected }

    val input = readInput("Day$DAY").map { it.toEquation() }
    println(part1(input))
    println(part2(input))
}

private fun String.toEquation(): Equation {
    val result = substringBefore(":").toLong()
    val operands = substringAfter(": ").split(" ").map { it.toLong() }
    return Equation(result, operands)
}

typealias Operator = (Long, Long) -> Long

private data class Equation(val expected: Long, val operands: List<Long>) {

    fun isSolvable(availableOperators: Set<Operator>, chosenOperators: List<Operator> = emptyList()): Boolean {
        if (chosenOperators.size + 1 == operands.size) {
            val result = operands.reduceIndexed { index, acc, l ->
                val operator = chosenOperators[index - 1]
                operator(acc, l)
            }
            return expected == result
        }
        return availableOperators.any { op ->
            isSolvable(availableOperators, chosenOperators + listOf(op))
        }
    }
}