package y23

import kotlin.math.pow

private const val DAY = "04"

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { it.toScratchCard().scorePart1() }
    }

    fun part2(input: List<String>): Int {
        val matches = input.mapIndexed { index, s -> index to s.toScratchCard().matchingCount() }.toMap()
        val cardCounts: MutableMap<Int, Int> = input.indices.associateWith { 1 }.toMutableMap()
        val indices = input.indices
        for (index in indices) {
            val count = cardCounts[index]?:1
            val won = matches[index]?:0
            for (j in index+1..index+won) {
                if (j in indices)
                    cardCounts[j] = cardCounts[j]!! + count
            }
        }
        return indices.sumOf { cardCounts[it]?:1 }
    }

    // test if implementation meets criteria from the description, like:
    try {
        val testInput = """
             Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
             Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
             Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
             Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
             Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
             Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
         """.trimIndent().split("\n")
        val part1 = part1(testInput)
        println(part1)
        check(part1 == 13)
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

private data class ScratchCard(val id: Int, val winningNumbers: List<Int>, val numbersYouHave: List<Int>) {
    private fun matching(): List<Int> = numbersYouHave.filter { it in winningNumbers }

    fun matchingCount() = matching().size

    fun scorePart1() = when (val n = matchingCount()) {
        0 -> 0
        else -> 2.0.pow(n - 1).toInt()
    }
}


private fun String.toScratchCard(): ScratchCard {
    val (prefix, winners, own) = split("""\s*[|:]\s*""".toRegex())
    val id = prefix.substringAfter("Card").trim().toInt()
    val winningNumbers = winners.toInts()
    val numbersYouHave = own.toInts()
    return ScratchCard(id, winningNumbers, numbersYouHave)
}

private fun String.toInts(): List<Int> =
    split(""" +""".toRegex()).mapNotNull { it.trim().toIntOrNull() }
