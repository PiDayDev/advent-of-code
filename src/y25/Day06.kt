package y25

private const val DAY = "06"

fun main() {

    fun plus(a: Long, b: Long) = a + b
    fun times(a: Long, b: Long) = a * b
    fun String.toOperation() = when (this) {
        "+" -> ::plus
        else -> ::times
    }

    fun part1(input: List<String>): Long {
        fun String.splitOnSpaces() = split("""\s+""".toRegex())

        val operations = input.last().splitOnSpaces().map(String::toOperation)
        val operands = input.dropLast(1).map { it.splitOnSpaces().map(String::toLong) }

        val results = operations.mapIndexed<(Long, Long) -> Long, Long> { index, op ->
            val items = operands.map { it[index] }
            items.reduce(op)
        }
        return results.sum()
    }

    fun part2(input: List<String>): Long {
        val maxIndex = input.maxOf { it.lastIndex }
        val newValues = (maxIndex downTo 0).map { index ->
            val column = input.map { row -> row.getOrElse(index) { ' ' } }
            column.joinToString("").trim()
        }

        fun compute(problem: List<String>): Long {
            val operation = problem.last().takeLast(1).toOperation()
            val operands = problem.map { it.replace("""\D""".toRegex(), "") }.map(String::toLong)
            return operands.reduce(operation)
        }

        fun total(problems: List<String>): Long {
            if (problems.isEmpty()) return 0L
            val pre = problems.takeWhile(String::isNotBlank)
            val suf = problems.drop(pre.size + 1)
            return compute(pre) + total(suf)
        }

        return total(newValues)
    }

    val input = readInput("Day$DAY").map { it.trim() }

    println(part1(input))
    println(part2(input))
}

