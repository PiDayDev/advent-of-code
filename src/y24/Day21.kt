package y24

import kotlin.math.absoluteValue

private const val DAY = "21"

fun main() {
    test()

    val input = readInput("Day$DAY")

    val p1 = input.sumOf { complexity(it, intermediateRobotCount = 2) }
    println("PART 1: $p1")
    check(p1 == 202274L)

    val p2 = input.sumOf { complexity(it, intermediateRobotCount = 25) }
    println("PART 2: $p2")
    check(p2 == 245881705840972L)
}

private fun complexity(sequence: String, intermediateRobotCount: Int): Long {
    val result = bestLength(sequence, intermediateRobotCount)
    val numberPart = sequence.removeSuffix("A").toLong(10)
    return result * numberPart
}

private fun bestLength(sequence: String, intermediateRobotCount: Int): Long = NumericalKeyboard
    .dial(sequence)
    .sumOf { alternatives ->
        alternatives.minOf {
            DirectionalKeyboard.costOfFullStep(it, intermediateRobotCount)
        }
    }

private typealias AlternativeSequences = List<String>

private object NumericalKeyboard {
    private val keys: Map<Char, Position> = """
        789
        456
        123
         0A
     """.trimIndent()
        .lines()
        .classifyByChar()
        .mapValues { (_, v) -> v.first() }

    fun dial(sequence: CharSequence): List<AlternativeSequences> = "A$sequence"
        .zipWithNext { a, b -> getPaths(keys[a]!!, keys[b]!!).map { "${it}A" } }

    private fun getPaths(from: Position, to: Position): List<String> {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val xPart = (if (dx > 0) ">" else "<").repeat(dx.absoluteValue)
        val yPart = (if (dy > 0) "v" else "^").repeat(dy.absoluteValue)

        return when {
            from.y == 3 && to.x == 0 -> listOf("$yPart$xPart")
            from.x == 0 && to.y == 3 -> listOf("$xPart$yPart")
            else -> listOf("$xPart$yPart", "$yPart$xPart")
        }
    }
}

private object DirectionalKeyboard {
    /**
     * For each movement from key X to key Y, the string "$X$Y" is mapped to THE minimal
     *  instruction sequence on its commanding robot that will produce that movement (except final A press).
     */
    private val instructionsByMove: Map<String, String> = mapOf(
        "AA" to "", "A^" to "<", "A>" to "v", "Av" to "<v", "A<" to "v<<",
        "^A" to ">", "^^" to "", "^>" to "v>", "^v" to "v", "^<" to "v<",
        ">A" to "^", ">^" to "<^", ">>" to "", ">v" to "<", "><" to "<<",
        "vA" to "^>", "v^" to "^", "v>" to ">", "vv" to "", "v<" to "<",
        "<A" to ">>^", "<^" to ">^", "<>" to ">>", "<v" to ">", "<<" to "",
    )

    private val costCache = mutableMapOf<Pair<String, Int>, Long>()
    private fun getCachedCost(s: String, level: Int) = costCache[s to level]
    private fun setCachedCost(s: String, level: Int, value: Long) {
        costCache[s to level] = value
    }

    fun costOfFullStep(s: String, level: Int): Long {
        val cost = getCachedCost(s, level)
            ?: when {
                level == 0 -> s.length.toLong()
                s == "A" -> 1L
                else -> "A$s"
                    .windowed(2)
                    .sumOf { costOfFullStep("${instructionsByMove[it]}A", level - 1) }
            }
        return cost.also {
            setCachedCost(s, level, it)
        }
    }
}

private fun test() {
    val testSequences = """
        029A
        980A
        179A
        456A
        379A
    """.trimIndent().lines()
    val testResults = testSequences.map { bestLength(it, 2) }
    check(testResults[0] == 68L) { "Size #0 is ${testResults[0]} instead of 68 // instruction 029A" }
    check(testResults[1] == 60L) { "Size #1 is ${testResults[1]} instead of 60 // instruction 980A" }
    check(testResults[2] == 68L) { "Size #2 is ${testResults[2]} instead of 68 // instruction 179A" }
    check(testResults[3] == 64L) { "Size #3 is ${testResults[3]} instead of 64 // instruction 456A" }
    check(testResults[4] == 64L) { "Size #4 is ${testResults[4]} instead of 64 // instruction 379A" }
}
