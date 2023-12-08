package y23

private const val DAY = "08"

typealias Bifurcation = Pair<String, String>

private data class DesertNetwork(
    val network: Map<String, Bifurcation>,
    var position: String = "AAA"
) {

    constructor(rows: List<String>) : this(
        rows.associate {
            val (src, left, right) = it.split("""[^A-Z]+""".toRegex())
            src to (left to right)
        }
    )

    fun navigate(direction: Char) {
        val destinations = network[position]!!
        position = when (direction) {
            'L' -> destinations.first
            else -> destinations.second
        }
    }
}


fun main() {
    fun mapAndSteps(input: List<String>): Pair<DesertNetwork, Sequence<Char>> {
        val directions: Sequence<Char> = input.first().asSequence()
        val map = DesertNetwork(input.drop(2))
        val steps: Sequence<Char> = generateSequence { directions }.flatten()
        return Pair(map, steps)
    }

    fun part1(input: List<String>): Int {
        val (map:DesertNetwork, steps: Sequence<Char>) = mapAndSteps(input)

        var count = 0
        val iterator = steps.iterator()

        while (map.position != "ZZZ") {
            map.navigate(iterator.next())
            count++
        }

        return count
    }

    fun part2(input: List<String>): Int {
        val (ghost:DesertNetwork, steps: Sequence<Char>) = mapAndSteps(input)
        val startingPoints = input.map { it.substringBefore(" =")}.filter { it.endsWith("A") }

        val ghosts = startingPoints.map { ghost.copy(position = it) }

        var count = 0
        val iterator = steps.iterator()

        while (ghosts.any{!it.position .endsWith("Z")}) {
            val step = iterator.next()
            ghosts.forEach { it.navigate(step) }
            count++
        }

        return count
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
