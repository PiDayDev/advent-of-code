package y23

import kotlin.math.pow

private const val DAY = "07"

private const val handSize = 5

private fun scoreGroups(groupSizes: Collection<Int>): Int {
    val sizes = groupSizes.sortedDescending()
    return (0 until handSize).fold(0) { score, index ->
        val increment = sizes.getOrElse(index) { 0 }
        increment + score * handSize
    }
}

private open class CamelHand(cards: String, open val bid: Int, val cardValues: String = "AKQJT98765432") {
    open val comboScore = scoreGroups(cards.groupingBy { it }.eachCount().values)

    val cardsScore = cards.fold(0) { score, card ->
        val increment = cardValues.indexOf(card)
        increment + score * cardValues.length
    }
}

private class JokerHand(cards: String, override val bid: Int) : CamelHand(cards, bid, "AKQT98765432J") {
    private val jokerCount =
        cards.count { it == 'J' }

    private val groupSizes =
        cards.filter { it != 'J' }.groupBy { it }.values.map { it.size }

    override val comboScore =
        scoreGroups(groupSizes) + jokerCount * handSize.toDouble().pow(4).toInt()
}

fun main() {
    fun solve(input: List<String>, buildHand: (cards: String, bid: Int) -> CamelHand): Int {
        val sortedHands = input
            .map { it.split(" ") }
            .map { (cards, bid) -> buildHand(cards, bid.toInt()) }
            .sortedWith(
                Comparator
                    .comparing { hand: CamelHand -> hand.comboScore }
                    .thenComparing { hand: CamelHand -> -hand.cardsScore }
            )

        return sortedHands
            .mapIndexed { index, camelHand -> (index + 1) * camelHand.bid }
            .sum()
    }

    fun part1(input: List<String>) = solve(input) { cards, bid -> CamelHand(cards, bid) }

    fun part2(input: List<String>) = solve(input) { cards, bid -> JokerHand(cards, bid) }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
