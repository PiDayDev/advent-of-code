package y24

private const val DAY = "05"

fun main() {
    val input = readInput("Day$DAY")

    val rules = input.takeWhile { it.isNotBlank() }.map { it.toRule() }
    val updates = input.takeLastWhile { it.isNotBlank() }.map { it.toUpdate() }

    val (valid, invalid) = updates.partition { update ->
        rules.all { rule -> rule.validate(update) }
    }

    fun part1(valid: List<Update>): Int =
        valid.sumOf { it.middle() }

    fun part2(invalid: List<Update>, rules: Set<Rule>): Int {
        fun compare(x: Int, y: Int) = when {
            Rule(before = x, after = y) in rules -> -1
            Rule(before = y, after = x) in rules -> +1
            else -> 0
        }
        return invalid
            .map { it.sortedWith(::compare) }
            .sumOf { it.middle() }
    }

    println(part1(valid))
    println(part2(invalid, rules.toSet()))
}

private data class Rule(val before: Int, val after: Int) {
    fun validate(update: Update): Boolean {
        val b = update.indexOf(before)
        val a = update.indexOf(after)
        return b < 0 || a < 0 || b < a
    }
}
private typealias Update = List<Int>

private fun String.toRule() = split("|").let { (b, a) -> Rule(b.toInt(), a.toInt()) }
private fun String.toUpdate() = split(",").map { it.toInt() }

private fun <T> List<T>.middle() = elementAt(indices.last / 2)
