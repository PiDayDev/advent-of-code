package y25

import org.chocosolver.solver.Model
import org.chocosolver.solver.search.strategy.Search
import org.chocosolver.solver.variables.IntVar

private const val DAY = "10"

private data class Lights(val seq: List<Boolean>) {
    fun press(button: Button) =
        Lights(seq.mapIndexed { j, b -> if (j in button) !b else b })

    fun off() = Lights(seq.map { false })
}

private data class Button(val seq: Set<Int>) {
    operator fun contains(n: Int) = n in seq
    override fun toString() = seq.joinToString(",", prefix = "<", postfix = ">")
}

private data class Joltage(val seq: List<Int>) {
    override fun toString() = "Joltage$seq"
}

private data class Machine(val lights: Lights, val buttons: List<Button>, val joltage: Joltage) {

    fun getMostEfficientLightTurnOn(): Int {
        val best = (1 until 1.shl(buttons.size))
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

    fun getMostEfficientJoltageTurnOn(): Int {
        val model = Model()
        val xs = buttons.mapIndexed { index, button ->
            val maxValue = button.seq.minOf { joltage.seq[it] }
            model.intVar("x$index", 0, maxValue)
        }

        val goal = model.intVar("goal", 0, joltage.seq.sum())
        joltage.seq.forEachIndexed { index, jolt ->
            val list = mutableListOf<IntVar>()
            buttons.forEachIndexed { j, button ->
                if (index in button.seq) {
                    list.add(xs[j])
                }
            }
            model.sum(list.toTypedArray(), "=", jolt).post()
        }
        model.sum(xs.toTypedArray(), "=", goal).post()
        val solver = model.solver

        solver.setSearch(Search.minDomUBSearch(*xs.toTypedArray()))

        print("$this => ")
        val solution = solver.findOptimalSolution(goal, Model.MINIMIZE)

        return solution.getIntVal(goal).also(::println)
    }

    override fun toString(): String =
        "Machine(${lights.seq.joinToString("") { if (it) "#" else "." }}, buttons=$buttons, $joltage)"
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
    fun part1(machines: List<Machine>): Int =
        machines.sumOf { it.getMostEfficientLightTurnOn() }

    // WARNING: SLOW - it takes several minutes
    fun part2(machines: List<Machine>): Int =
        machines
            .sortedBy { it.joltage.seq.size + it.buttons.size }
            .mapIndexed { index, machine ->
                print("$index: ")
                machine.getMostEfficientJoltageTurnOn()
            }
            .sum()

    val input = readInput("Day$DAY").map(String::parse)
    println(part1(input))
    println(part2(input))
}
