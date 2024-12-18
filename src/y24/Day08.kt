package y24

import kotlin.math.max

private const val DAY = "08"

fun main() = readInput("Day$DAY")
    .toAntennas()
    .run {
        println(antinodesPart1().size)
        println(antinodesPart2().size)
    }

private class Antennas(
    private val width: Int,
    private val height: Int,
    private val map: Map<Char, List<Position>>,
) {

    fun antinodesPart1() = foldOnAllKeys { first, second ->
        setOf(first.antinodeFor(second), second.antinodeFor(first))
    }

    fun antinodesPart2() = foldOnAllKeys { first, second ->
        val dx = first.x - second.x
        val dy = first.y - second.y
        val maxMultiplier = max(width, height)
        val list = (-maxMultiplier..maxMultiplier).map { Position(first.x + it * dx, first.y + it * dy) }
        list.toSet()
    }

    private fun foldOnAllKeys(allAntinodesFor: (Position, Position) -> Set<Position>) =
        map.keys.fold<Char, Set<Position>>(emptySet()) { acc, k ->
            val emitters = map[k] ?: emptyList()
            val result = mutableSetOf<Position>()
            emitters.indices.forEach { i ->
                (i + 1..emitters.lastIndex).forEach { j ->
                    result += allAntinodesFor(emitters[i], emitters[j])
                }
            }
            acc + result.filter { it.isValid() }
        }

    private fun Position.antinodeFor(other: Position): Position {
        val dx = other.x - x
        val dy = other.y - y
        return Position(x + 2 * dx, y + 2 * dy)
    }

    private fun Position.isValid(): Boolean = x in 0 until width && y in 0 until height
}

private fun List<String>.toAntennas(): Antennas {
    val height = count()
    val width = first().count()
    val map = classifyByChar()
    return Antennas(width, height, map.filterKeys { it != '.' })
}

