package y24

import y24.Gate.Companion.GateRole.*
import java.util.*
import java.util.Comparator.comparing
import kotlin.random.Random

private const val DAY = "24"

private const val HIGHEST_BIT = 45

fun main() {

    val input = readInput("Day$DAY")

    val (xInput, yInput) = input.takeWhile { it.isNotBlank() }.toInputs()

    val circuit = input.takeLastWhile { it.isNotBlank() }.toCircuit()

    // Fun fact #1: part 1 is way faster to solve in Excel
    val part1 = circuit.compute(xInput, yInput).toLong()
    println("PART 1: $part1")
    check(part1 == 59619940979346L)

    // Fun fact #2: I solved part 2 initially by hand... as most other coders.
    val outputsToSwap = findFaultyOutputsForRippleCarryAdder(circuit.gates)
    // Check on a few samples that analytic solution leads to a working circuit
    val possiblePairs: Set<Set<Set<String>>> = permutations(outputsToSwap)
        .asSequence()
        .map { it.chunked(2).map { o -> o.toSet() }.toSet() }
        .toSet()
    val inputUntil = 1L shl HIGHEST_BIT
    val validSwaps = possiblePairs.firstOrNull { swaps ->
        try {
            val newCircuit = swaps.fold(circuit) { c, swap ->
                c.withSwappedOutputs(swap.first(), swap.last())
            }
            repeat(100) {
                val (x, y) = List(2) { Random.nextLong(inputUntil) }
                val z = x + y
                check(z.toBitSet() == newCircuit.compute(x.toBitSet(), y.toBitSet()))
            }
            true
        } catch (e: IllegalStateException) {
            false
        }
    }
    println("\nPART 2 is solved by swaps $validSwaps")

    val part2 = outputsToSwap.sorted().joinToString(",")
    println("PART 2: $part2")
    val mySolution = "bpt,fkp,krj,mfm,ngr,z06,z11,z31"
    check(part2 == mySolution) { "Solution is $mySolution" }
}

private fun Long.toBitSet() = BitSet.valueOf(longArrayOf(this))
private fun BitSet.toLong() = toLongArray().first()

private typealias Wire = String

private data class Gate(val opName: String, val input1: Wire, val input2: Wire, val output: Wire) {
    companion object {
        enum class GateRole { XOR_INPUT, AND_INPUT, XOR_SUM, OR_CARRY, AND_CARRY }
    }

    val op: (Boolean, Boolean) -> Boolean = when (opName) {
        "AND" -> Boolean::and
        "XOR" -> Boolean::xor
        else -> Boolean::or
    }

    val isXY = "x" in input1.take(1) + input2.take(1)

    val isZ = output.startsWith("z")

    val role: GateRole = when (opName) {
        "AND" -> if (isXY) AND_INPUT else AND_CARRY
        "XOR" -> if (isXY) XOR_INPUT else XOR_SUM
        else -> OR_CARRY
    }

    override fun toString(): String = "$input1 $opName $input2 => $output"
}

private data class Circuit(val gates: List<Gate>) {
    fun compute(x: BitSet, y: BitSet): BitSet {
        val map = (x.toMap('x') + y.toMap('y')).toMutableMap()
        gates.forEach { gate ->
            val (_, input1, input2, output) = gate
            val v1 = map[input1] ?: false
            val v2 = map[input2] ?: false
            map[output] = gate.op(v1, v2)
        }
        val z = BitSet()
        map
            .filterKeys { it.startsWith("z") }
            .forEach { (k, v) -> if (v) z.set(k.drop(1).toInt()) }
        return z
    }

    private fun BitSet.toMap(prefix: Char): Map<String, Boolean> =
        (0..HIGHEST_BIT).associate { j ->
            val key = "$prefix" + "$j".padStart(2, '0')
            val value = get(j)
            key to value
        }

    fun withSwappedOutputs(o1: Wire, o2: Wire): Circuit = gates
        .map { gate ->
            when (gate.output) {
                o1 -> gate.copy(output = o2)
                o2 -> gate.copy(output = o1)
                else -> gate
            }
        }
        .sortedByPriority()
        .let(::Circuit)
}

private fun List<String>.toInputs(): Pair<BitSet, BitSet> {
    val x = BitSet()
    val y = BitSet()
    fun parse(row: String) = row.drop(1).split(": ").map { it.toInt() }

    val (xRows, yRows) = partition { it.startsWith("x") }
    xRows.map { parse(it) }.filter { (_, v) -> v > 0 }.forEach { (j) -> x.set(j) }
    yRows.map { parse(it) }.filter { (_, v) -> v > 0 }.forEach { (j) -> y.set(j) }

    return x to y
}

private fun List<Gate>.sortedByPriority(): List<Gate> {
    val rest = sortedWith(comparing(Gate::input1).thenComparing(Gate::input2)).toMutableList()
    val output = mutableListOf<Gate>()
    val validInputs = (0..HIGHEST_BIT)
        .map { "$it".padStart(2, '0') }
        .flatMap { listOf("x$it", "y$it") }
        .toMutableSet()
    while (rest.isNotEmpty()) {
        val sizeBefore = rest.size
        val iter = rest.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.input1 in validInputs && next.input2 in validInputs) {
                iter.remove()
                output += next
                validInputs += next.output
            }
        }
        if (rest.size == sizeBefore) throw IllegalStateException("Circular reference detected")
    }
    return output
}

private fun List<String>.toCircuit(): Circuit {
    val gates = map { row ->
        val (left, op, right, _, output) = row.split(" ")
        val (input1, input2) = listOf(left, right).sorted()
        Gate(opName = op, input1 = input1, input2 = input2, output = output)
    }
    return Circuit(gates.sortedByPriority())
}

/*
The desired circuit is a <ripple-carry adder>, made of a series of <full adders> that link inputs x(n) and y(n) to
 outputs sum z(n) and carry-out cout(n), that in turn becomes the carry-in for next adder in the series.
Exceptions:
 - First circuit is a half-adder, without carry-in.
 - z(45) is actually just cout(44)

       +--->\\-----\
       |     || XOR >----+---------------------> \\-----\
       | +->//-----/     |                        || XOR >--------------------- z(n)
       | |               |   cout(n-1) --+-----> //-----/
 x(n)--+ |               |               |
       | |               |               +-----> |------\
 y(n)--]-+               |                       |  AND  >----+
       | |               +---------------------> |------/     |     \----\
       | +-> |------\                                         +--->  | OR >---- cout(n)
       |     |  AND  >--------------------------------------------> /----/
       +---> |------/
 */
private fun findFaultyOutputsForRippleCarryAdder(gates: List<Gate>): List<String> = gates
    .filter { gate ->
        val validForRole = when (gate.role) {
            XOR_SUM -> gate.isZ
            OR_CARRY -> gate.output == "z$HIGHEST_BIT" || !gate.isZ
            AND_CARRY -> !gate.isZ
            XOR_INPUT -> gate.output == "z00" || !gate.isZ && (
                    // XOR gates must not feed OR gates directly
                    gates
                        .filter { it.role == OR_CARRY }
                        .none { other -> gate.output in listOf(other.input1, other.input2) }
                    )

            AND_INPUT -> !gate.isZ && (
                    // AND gates must not feed XOR gates, except for first half-adder
                    "00" in gate.input1 || gates
                        .filter { it.role == XOR_SUM }
                        .none { other -> gate.output in listOf(other.input1, other.input2) }
                    )

        }
        !validForRole
    }
    .map { it.output }

