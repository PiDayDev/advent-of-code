package y25

private const val DAY = "10"

private data class Lights(val seq: List<Boolean>) {
    constructor(n: Int) : this(List(n) { false })

    fun press(button: Button) =
        Lights(seq.mapIndexed { j, b -> if (j in button) !b else b })

    fun off() = Lights(seq.size)
}

private data class Button(val seq: Set<Int>) {
    operator fun contains(n: Int) = n in seq
}

private data class Joltage(val seq: List<Int>)

private data class Machine(val lights: Lights, val buttons: List<Button>, val joltage: Joltage) {
    fun getMostEfficientLightTurnOn(): Int {
        val best = (0 until 1.shl(buttons.size))
            .mapNotNull { bitMask ->
                val result = buttons.foldIndexed(lights.off()) { index, acc, button ->
                    when {
                        1.shl(index) and bitMask > 0 -> acc.press(button)
                        else -> acc
                    }
                }
                when (result) {
                    lights -> bitMask.countOneBits()
                    else -> null
                }
            }
            .min()
        return best
    }
}

private fun String.parse(): Machine {
    val lightString = substringBefore(" ").removeSurrounding("[", "]")
    val lights = Lights(lightString.map { it == '#' })
    val buttonString = substringAfter(" ").substringBefore(" {")
    val buttons = buttonString.split(" ")
        .map { it.removeSurrounding("(", ")").split(",").map(String::toInt) }
        .map { Button(it.toSet()) }
    val joltageString = substringAfter("{").substringBefore("}")
    val joltage = joltageString.split(",").map(String::toInt).let { Joltage(it) }
    return Machine(lights, buttons, joltage)
}

fun main() {
    fun part1(input: List<Machine>): Int =
        input.sumOf { it.getMostEfficientLightTurnOn() }

    fun part2(input: List<Machine>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description
    /* try {
         val testInput = readInput("Day${DAY}_test")
         check(part1(testInput) == 1)
     } catch (e: java.io.FileNotFoundException) {
         // no tests
     }*/

    val input = readInput("Day$DAY").map(String::parse)
    println(part1(input))
    println(part2(input))
}
