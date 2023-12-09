package y23

private const val DAY = "08"

typealias Fork = Pair<String, String>

private data class DesertNetwork(
    val network: Map<String, Fork>,
    val origin: String = "AAA"
) {
    var position = origin
        private set

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
        val map = DesertNetwork(input.drop(2))

        val directions = input.first().asSequence()
        val steps: Sequence<Char> = generateSequence { directions }.flatten()

        return map to steps
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
        val startingPoints = input.map { it.substringBefore(" = ") }.filter { it.endsWith("A") }

        val ghosts = startingPoints.map { ghost.copy(origin = it) }

        var count = 0L
        val iterator = steps.iterator()

        val loopLengths = mutableMapOf<String, Long>()
        while (loopLengths.size < startingPoints.size) {
            count++
            val step = iterator.next()
            ghosts.forEach {
                it.navigate(step)
                if (it.position.endsWith("Z") && it.origin !in loopLengths) {
                    loopLengths[it.origin] = count
                }
            }
        }

        return lcm(*loopLengths.values.toLongArray())
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}

private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

private fun lcm(vararg n: Long): Long = n.reduce { a, b ->
    a * b / gcd(a, b)
}
