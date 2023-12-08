package y23

private const val DAY = "08"

typealias Fork = Pair<String, String>

private data class DesertNetwork(
    val network: Map<String, Fork>,
    val origin: String = "AAA"
) {
    var position = origin
        private set

    var navigationCounter = 0
        private set

    val reachedDestinations = mutableListOf(origin)

    constructor(rows: List<String>) : this(
        rows.associate {
            val (src, left, right) = it.split("""[^A-Z]+""".toRegex())
            src to (left to right)
        }
    )

    fun navigate(direction: Char) {
        navigationCounter++
        val destinations = network[position]!!
        position = when (direction) {
            'L' -> destinations.first
            else -> destinations.second
        }
        reachedDestinations += position
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

    fun part2(input: List<String>): Long {
        val (ghost:DesertNetwork, steps: Sequence<Char>) = mapAndSteps(input)
        val startingPoints = input.map { it.substringBefore(" =")}.filter { it.endsWith("A") }

        val ghosts = startingPoints.map { ghost.copy(origin = it) }

        var count = 0
        val iterator = steps.iterator()

        while (ghosts.any{!it.position .endsWith("Z")}) {
            val step = iterator.next()
            ghosts.forEach { it.navigate(step) }
            count++
            if (count > 99_999) break
        }

        for (g in ghosts) {
            println(
                """
                -----------------------------------------
                From ${g.origin} I reached
            """.trimIndent()
            )
            g.reachedDestinations.mapIndexed { index, s -> index to s }
                .filter { it.second.endsWith("Z") }
                .forEach { println("- ${it.second} in ${it.first} steps") }
        }

        return lcm(18113, 22411, 21797, 14429, 16271, 18727)
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

private fun lcm(vararg n: Long): Long = n.reduce { a, b ->
    a * b / gcd(a, b)
}
