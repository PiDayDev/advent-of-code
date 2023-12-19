package y23

import java.util.*

private const val DAY = "19"

typealias Part = Map<String, Int>

enum class Comparison { LT, GT }

sealed class Rule(val destination: String) {
    open fun matches(part: Part) = true
    open fun partition(hyperpart: Hyperpart): Pair<Hyperpart, Hyperpart?> = hyperpart to null
}

class ConditionalRule(
    private val property: String,
    private val sign: Comparison,
    val value: Int,
    destination: String
) :
    Rule(destination) {
    override fun matches(part: Part): Boolean {
        val v = part[property]!!
        return when (sign) {
            Comparison.LT -> v < value
            Comparison.GT -> v > value
        }
    }

    private val lower = when (sign) {
        Comparison.LT -> value - 1
        Comparison.GT -> value
    }

    private val upper = when (sign) {
        Comparison.LT -> value
        Comparison.GT -> value + 1
    }

    override fun partition(hyperpart: Hyperpart): Pair<Hyperpart, Hyperpart?> {
        val range: IntRange = hyperpart[property]!!
        val left: IntRange = range.first..lower
        val right: IntRange = upper..range.last
        check(left.count() + right.count() == range.count())

        val hRight: Map<String, IntRange> = hyperpart + (property to right)
        val hLeft: Map<String, IntRange> = hyperpart + (property to left)
        return when (sign) {
            Comparison.GT -> hRight to hLeft
            Comparison.LT -> hLeft to hRight
        }
    }
}

class ElseRule(destination: String) : Rule(destination)

data class Workflow(val key: String, val rules: List<Rule>) {
    fun process(part: Part) = rules.first { it.matches(part) }

    fun process(hyperpart: Hyperpart): List<Pair<Hyperpart, String>> {
        val result = mutableListOf<Pair<Hyperpart, String>>()
        rules.fold<Rule, Hyperpart?>(hyperpart) { hp, rule ->
            if (hp == null) null
            else {
                val (ok, ko) = rule.partition(hp)
                result += ok to rule.destination
                if ((ko?.countParts() ?: 0L) > 0L) ko else null
            }
        }
        return result.filter { it.first.countParts() > 0L }
    }
}

fun toPart(string: String): Part = string
    .removeSurrounding("{", "}")
    .split(",")
    .associate {
        val (k, v) = it.split("=")
        k to v.toInt()
    }

fun toRule(string: String): Rule {
    if ("<" in string || ">" in string) {
        val (prop, num, dest) = string.split("""[<>:]""".toRegex())
        val comparison = when (string.substringAfter(prop).take(1)) {
            ">" -> Comparison.GT
            else -> Comparison.LT
        }
        return ConditionalRule(prop, comparison, num.toInt(), dest)
    }
    return ElseRule(string)
}

fun toWorkflow(string: String): Workflow {
    val (id, rulesString) = string.split("""[}{]""".toRegex())
    val rules = rulesString.split(",").map { toRule(it) }
    return Workflow(id, rules)
}

typealias Hyperpart = Map<String, IntRange>

private fun Hyperpart.countParts(): Long = values.fold(1L) { p, r -> p * r.count().toLong() }

fun main() {
    tailrec fun Map<String, Workflow>.process(part: Part, workflowKey: String = "in"): Int =
        when (val nextKey = get(workflowKey)!!.process(part).destination) {
            "A" -> part.values.sum()
            "R" -> 0
            else -> process(part, nextKey)
        }

    fun part1(input: List<String>): Int {
        val (w, p) = input.splitOn { it.isBlank() }
        val parts = p.map(::toPart)
        val workflows: Map<String, Workflow> = w.map(::toWorkflow).associateBy { it.key }
        return parts.sumOf { part -> workflows.process(part) }
    }

    fun part2(input: List<String>): Long {
        val (w) = input.splitOn { it.isBlank() }
        val workflows: Map<String, Workflow> = w.map(::toWorkflow).associateBy { it.key }
        val initial: Hyperpart = "xmas".associate { "$it" to 1..4000 }

        val queue = LinkedList(listOf(initial to "in"))
        var accepted = 0L
        while (queue.isNotEmpty()) {
            val (hyperpart, destination) = queue.poll()!!
            when (destination) {
                "A" -> accepted += hyperpart.countParts()
                "R" -> accepted += 0L
                else -> queue += workflows[destination]!!.process(hyperpart)
            }
        }
        return accepted
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
