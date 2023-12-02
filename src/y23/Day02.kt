package y23

import kotlin.math.max

private const val DAY = "02"

fun main() {
    fun part1(input: List<String>) = input
        .filterNot { it.isImpossible() }
        .map { it.substringAfter("Game ").substringBefore(":") }
        .sumOf { it.toInt() }

    fun part2(input: List<String>) = input
        .map { it.toFullGame() }
        .sumOf { it.power }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

private fun String.isImpossible() =
    """Game \d+:.*((1[3-9]|[2-9]\d)\d* red|(1[4-9]|[2-9]\d)\d* green|(1[5-9]|[2-9]\d)\d* blue).*""".toRegex().matches(this)

private data class Game(val red: Int = 0, val green: Int = 0, val blue: Int = 0) {
    val power = red * green * blue
}

private fun String.toFullGame(): Game =
    substringAfter(":").split(";").map { it.toSubGame() }.fold(Game()) { a, b ->
        Game(max(a.red, b.red), max(a.green, b.green), max(a.blue, b.blue))
    }

private fun String.toSubGame(): Game = this
    .trim()
    .split(",")
    .fold(Game()) { game, string ->
        val number = string.trim().substringBefore(" ").toInt()
        when {
            string.endsWith("red") -> game.copy(red = number)
            string.endsWith("green") -> game.copy(green = number)
            string.endsWith("blue") -> game.copy(blue = number)
            else -> game
        }
    }
