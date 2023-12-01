package y22

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive

private const val DAY = 13

sealed interface PacketItem13 : Comparable<PacketItem13>

class Number13(val n: Int) : PacketItem13 {
    override operator fun compareTo(other: PacketItem13): Int =
        when (other) {
            is Number13 -> n.compareTo(other.n)
            is List13 -> -other.compareTo(this)
        }

    override fun toString() = "$n"
}

class List13(items: Collection<PacketItem13>) : List<PacketItem13> by ArrayList(items), PacketItem13 {
    constructor(item: Number13) : this(listOf(item))

    override operator fun compareTo(other: PacketItem13): Int =
        when (other) {
            is Number13 -> compareTo(List13(other))
            is List13 ->
                zip(other).asSequence().map { (a, b) -> a.compareTo(b) }.firstOrNull { it != 0 }
                    ?: size.compareTo(other.size)
        }

    override fun toString() = joinToString(separator = ",", prefix = "[", postfix = "]")
}

typealias Couple13 = Pair<List13, List13>

private fun String.toJsonArray() = Json.parseToJsonElement(this) as JsonArray

private fun JsonArray.convert(): List13 =
    List13(mapNotNull {
        when (it) {
            is JsonArray -> it.convert()
            is JsonPrimitive -> Number13(it.toString().toInt())
            else -> null
        }
    })

private fun makeDividerPacket(n: Int) = "[[$n]]".toPacket()

private fun String.toPacket() = toJsonArray().convert()

/**
 * Didn't want to spend too much time on parsing, so I transformed
 *  txt input into a Kotlin class using IDE replacement tools.
 */
fun main() {
    fun part1(input: List<Couple13>) = input
        .mapIndexed { index, (first, second) ->
            when {
                first < second -> index + 1
                else -> 0
            }
        }.sum()

    fun part2(input: List<Couple13>): Int {
        val divider2 = makeDividerPacket(2)
        val divider6 = makeDividerPacket(6)
        val allPackets = input.flatMap { it.toList() } + divider2 + divider6
        val sorted = allPackets.sorted()
        val idx2 = sorted.indexOfFirst { it.compareTo(divider2) == 0 }
        val idx6 = sorted.indexOfFirst { it.compareTo(divider6) == 0 }
        return (idx2 + 1) * (idx6 + 1)
    }

    val rows = readInput("Day$DAY")
        .chunked(3)
        .map { (a, b) ->
            a.toPacket() to b.toPacket()
        }

    println(part1(rows))
    println(part2(rows))
}

