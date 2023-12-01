package y22

import common.contains
import common.overlaps

private const val DAY = "04"

private fun String.toRangePair() = split(",").map { it.toRange() }.let { (a, b) -> a to b }

private fun String.toRange() = split("-").let { (a, b) -> a.toInt()..b.toInt() }

fun main() {
    val input = readInput("Day$DAY")

    val listOfRangePairs = input.map { it.toRangePair() }

    println(listOfRangePairs.count { (a, b) -> a in b || b in a })

    println(listOfRangePairs.count { (a, b) -> a overlaps b })
}
