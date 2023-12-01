package y22

import common.ArithmeticCalculator

private const val DAY = 21

const val delimiter = ": "

fun main() {

    val input = readInput("Day$DAY")

    fun toArithmeticExpression(monkeyStrings: Map<String, String>): String {
        var root = monkeyStrings["root"]!!
        val expression = generateSequence(1) { it + 1 }
            .map {
                monkeyStrings.forEach { (name, value) ->
                    val v = if (' ' in value) "($value)" else value
                    root = root.replace(name, v)
                }
                root
            }
            .dropWhile { it.uppercase() != it }
            .first()
        return expression
    }

    fun part1(monkeyStrings: Map<String, String>): Long {
        val expression = toArithmeticExpression(monkeyStrings)
        return ArithmeticCalculator(expression).calculate()
    }

    fun part2(monkeyStrings: Map<String, String>): Long? {
        val rootReplaced = monkeyStrings["root"]!!.replace("[-+*/]".toRegex(), "-")

        fun tryWithNumber(k: Long): Long {
            val replacements = mapOf("humn" to "$k", "root" to rootReplaced)
            val expression = toArithmeticExpression(monkeyStrings + replacements)
            return ArithmeticCalculator(expression).calculate()
        }

        // bisect
        var min = 0L
        var ymin = tryWithNumber(min)
        if (ymin == 0L) return min

        var max = 10e12.toLong()
        var ymax = tryWithNumber(max)
        if (ymax == 0L) return max

        while (max - min > 1000L) {
            val curr = (min + max) / 2
            val ycurr = tryWithNumber(curr)
            when {
                0L in ycurr..ymin || 0L in ymin..ycurr -> {
                    max = curr
                    ymax = ycurr
                }

                0L in ycurr..ymax || 0L in ymax..ycurr -> {
                    min = curr
                    ymin = ycurr
                }
            }
        }
        (min..max).forEach {
            if (tryWithNumber(it) == 0L)
                return it
        }
        return null
    }

    fun getMonkeyStrings(input: List<String>) = input.associate {
        val name = it.substringBefore(delimiter)
        val value = it.substringAfter(delimiter)
        name to value
    }

    val monkeyStrings = getMonkeyStrings(input)
    println(part1(monkeyStrings))
    println(part2(monkeyStrings))

}
