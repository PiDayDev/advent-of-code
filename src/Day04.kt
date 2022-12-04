private const val DAY = "04"

private fun String.toRangePair() = split(",").map { it.toRange() }.let { (a, b) -> a to b }

private fun String.toRange() = split("-").let { (a, b) -> a.toInt()..b.toInt() }

private operator fun IntRange.contains(other: IntRange) = other.first in this && other.last in this

private infix fun IntRange.overlaps(other: IntRange) = other.first in this || other.last in this || first in other || last in other

fun main() {

    val input = readInput("Day${DAY}")
    val listOfRangePairs = input.map { it.toRangePair() }

    println(listOfRangePairs.count { (a, b) -> a in b || b in a })

    println(listOfRangePairs.count { (a, b) -> a overlaps b })

}
