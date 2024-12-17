package y24

import y24.Instruction.*

private const val DAY = "17"

/** Opcode program translated into Kotlin code, with some refactoring */
private fun executeSpecialized(a: Long): MutableList<Int> {
    var curr = a
    val result = mutableListOf<Int>()
    while (curr != 0L) {
        val finalB = execStep(curr)
        result += finalB
        curr = curr shr 3
    }
    return result
}

private fun execStep(curr: Long): Int {
    val temp1 = ((curr xor 5L) and 7L).toInt()
    val temp2 = ((curr shr temp1) and 7L).toInt()
    val finalB = temp1 xor temp2 xor 6
    return finalB
}


fun main() {
    fun part1(input: List<String>): String {
        val computer = input.toThreeBitComputer()
        computer.execute()
        return computer.output.joinToString(",")
    }

    fun part1Specialized(input: List<String>): String {
        val a = input.first().substringAfter(": ").toLong()
        return executeSpecialized(a).joinToString(",")
    }

    fun part2Specialized(input: List<String>): Long {
        // group 13-bit numbers by the output they produce in a single step of the program
        val map: Map<Int, List<Long>> = (0L until (1L shl 13)).groupBy { execStep(it) }

        // desired output
        val program = input.toThreeBitComputer().program

        // will construct the input value for Register A by "merging" numbers
        //  that each give the next expected output

        // to merge two numbers, the first 10 bits of the previous number must equal the last 10 bits of the next number
        // => if they "overlap" this way, prepend the most-significant 3 bits of the next number to the previous
        fun mergeOne(previous: Long, next: Long, level: Int): Long? {
            val shift = 3 * level
            if (previous shr shift != next and 0b11111_11111) return null
            return (next shl shift) or previous
        }
        fun mergeLists(prevNumbers: List<Long>, nextNumbers: List<Long>, level: Int): List<Long> =
            prevNumbers.flatMap { prev ->
                nextNumbers.mapNotNull { next ->
                    mergeOne(prev, next, level)
                }
            }

        val steps = mutableListOf<List<Long>>()
        program.forEach { number -> steps += map[number]!! }

        val firstOutput = program.first()
        var candidates = map[firstOutput]!!
        for (n in 1..program.indices.last) {
            val nthOutput = program[n]
            candidates = mergeLists(candidates, map[nthOutput]!!, n)
        }
        return candidates
            .filter { executeSpecialized(it) == program }
            .min()
    }

    // test if implementation meets criteria from the description, like:
    val t1 = part1(
        """
               Register A: 729
               Register B: 0
               Register C: 0

               Program: 0,1,5,4,3,0
           """.trimIndent().lines()
    )
    println("TEST 1: $t1")
    check(t1 == "4,6,3,5,6,3,5,2,1,0")

    val input = readInput("Day$DAY")
    val p1 = part1(input)
    println("PART 1: $p1")
    check(p1 == "7,0,3,1,2,6,3,7,1")
    check(p1 == part1Specialized(input))

    val p2 = part2Specialized(input)
    println("PART 2: $p2")
    check(p2 == 109020013201563L)
}


private fun List<String>.toThreeBitComputer(): ThreeBitComputer {
    val (a, b, c) = take(3).map { it.substringAfter(": ").toLong() }
    val program = last { it.isNotBlank() }.substringAfter(": ")
        .split(",")
        .map { it.toInt() }
    return ThreeBitComputer(a, b, c, program)
}

private data class ThreeBitComputer(
    var a: Long = 0L,
    var b: Long = 0L,
    var c: Long = 0L,
    val program: List<Int>
) {
    val output = mutableListOf<Int>()
    var pointer = 0

    fun execute() {
        while (!halts()) executeOne()
    }

    private fun halts() = pointer !in program.indices

    private fun executeOne() {
        val instruction = Instruction.of(program[pointer])
        val rawOperand = program[pointer + 1]
        val operand =
            if (instruction.hasComboOperand) comboOperand(rawOperand)
            else rawOperand.toLong()

        var jumpSize = 2
        when (instruction) {
            ADV -> a = a shr operand.toInt()
            BDV -> b = a shr operand.toInt()
            CDV -> c = a shr operand.toInt()

            BXL -> b = b xor operand
            BXC -> b = b xor c
            BST -> b = operand and 7

            JNZ -> {
                if (a != 0L) {
                    jumpSize = 0
                    pointer = operand.toInt()
                }
            }

            OUT -> output.add((operand % 8).toInt())
        }
        pointer += jumpSize
    }

    fun comboOperand(n: Int) = when (n) {
        4 -> a
        5 -> b
        6 -> c
        7 -> throw IllegalArgumentException("Combo operand 7 unexpected at ${pointer + 1}")
        else -> n.toLong()
    }
}

private enum class Instruction(val opcode: Int, val hasComboOperand: Boolean) {
    ADV(0, true),
    BXL(1, false),
    BST(2, true),
    JNZ(3, false),
    BXC(4, false),
    OUT(5, true),
    BDV(6, true),
    CDV(7, true);

    companion object {
        private val map = values().associateBy { it.opcode }
        fun of(opcode: Int) = map[opcode]!!
    }
}
