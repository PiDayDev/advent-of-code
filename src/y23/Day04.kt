package y23

import kotlin.math.pow

private const val DAY = "04"

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { it.toScratchCard().scorePart1() }
    }

    fun part2(input: List<String>): Int {
        val matches = input.mapIndexed { index, s -> index to s.toScratchCard().matchingCount() }.toMap()
        val indices = input.indices
        val cardCounts: MutableMap<Int, Int> = indices.associateWith { 1 }.toMutableMap()
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
