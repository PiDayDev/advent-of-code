package y24

import java.util.*
import java.util.Comparator.comparing

private const val DAY = "24"

private const val HIGHEST_BIT = 45

fun main() {

    val input = readInput("Day$DAY")

    val (xInput, yInput) = input.takeWhile { it.isNotBlank() }.toInputs()

    val circuit = input.takeLastWhile { it.isNotBlank() }.toCircuit()

    // Fun fact #1: part 1 is way faster to solve in Excel
    val part1 = circuit.compute(xInput, yInput)
    println("PART 1: $part1")
    check(part1 == 59619940979346L)

    circuit.gates.forEach(::println)

    // Fun fact #2: I solved part 2 by hand after deducing it was a series of full-adder circuits.
    // Procedure: give a standard name to each wire in a full-adder circuit, using
    //            the same bit number as X/Y/Z bits, then find the outliers.
    val part2 = listOf("z06", "fkp", "z31", "mfm", "bpt", "krj", "ngr", "z11")
        .sorted().joinToString(",")
    println("PART 2: $part2")
    check(part2 == "bpt,fkp,krj,mfm,ngr,z06,z11,z31")
}

private typealias Wire = String

private data class Gate(val opName: String, val input1: Wire, val input2: Wire, val output: Wire) {
    val op: (Boolean, Boolean) -> Boolean = when (opName) {
        "AND" -> Boolean::and
        "XOR" -> Boolean::xor
        else -> Boolean::or
    }

    override fun toString(): String = "$input1 $op $input2 => $output"
}

private data class Circuit(val gates: List<Gate>) {
    private fun BitSet.toMap(prefix: Char): Map<String, Boolean> =
        (0..HIGHEST_BIT).associate { j ->
            val key = "$prefix" + "$j".padStart(2, '0')
            val value = get(j)
            key to value
        }

    fun compute(x: BitSet, y: BitSet): Long {
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
        return z.toLongArray().first()
    }

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

private fun List<String>.toCircuit(): Circuit {
    fun List<Gate>.sortedByPriority(): List<Gate> {
        val rest = sortedWith(comparing(Gate::input1).thenComparing(Gate::input2)).toMutableList()
        val output = mutableListOf<Gate>()
        val validInputs = (0..HIGHEST_BIT)
            .map { "$it".padStart(2, '0') }
            .flatMap { listOf("x$it", "y$it") }
            .toMutableSet()
        while (rest.isNotEmpty()) {
            val iter = rest.iterator()
            while (iter.hasNext()) {
                val next = iter.next()
                if (next.input1 in validInputs && next.input2 in validInputs) {
                    iter.remove()
                    output += next
                    validInputs += next.output
                }
            }
        }
        return output
    }

    val gates = map { row ->
        val (left, op, right, _, output) = row.split(" ")
        val (input1, input2) = listOf(left, right).sorted()
        Gate(opName = op, input1 = input1, input2 = input2, output = output)
    }
    return Circuit(gates.sortedByPriority())
}
