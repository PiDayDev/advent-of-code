package y24

private const val DAY = 25

fun main() {
    infix fun String.matches(other: String): Boolean =
        zip(other).none { (a, b) -> a == '#' && b == '#' }

    fun part1(input: List<String>): Int {
        val serializedItems = input.chunked(8).map { it.joinToString("") }
        val (locks, keys) = serializedItems.partition { it.startsWith("#") }

        return locks.sumOf { lock ->
            keys.count { key ->
                key matches lock
            }
        }
    }

    val input = readInput("Day$DAY")
    println(part1(input))
}
