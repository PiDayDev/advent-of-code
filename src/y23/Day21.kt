package y23

private const val DAY = "21"

fun main() {

    fun dijkstra(input: List<String>, start: Position, max: Int): Map<Position, Int> {
        val distances = mutableMapOf<Position, Int>()
        var frontier = setOf(start)
        var distance = 0

        while (frontier.isNotEmpty() && distance <= max) {
            frontier.forEach { distances[it] = distance }
            frontier = frontier
                .asSequence()
                .flatMap { it.around() }
                .filterNot { it in distances }
                .filter { (x, y) -> (input.getOrNull(y)?.getOrNull(x) ?: '#') in "S." }
                .toSet()
            distance++
        }
        return distances
    }

    fun part1(input: List<String>, goal: Int): Int {
        val yStart = input.indexOfFirst { 'S' in it }
        val xStart = input[yStart].indexOf('S')

        val distances = dijkstra(input, Position(xStart, yStart), goal)

        return distances.values.count { it % 2 == goal % 2 }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("Day$DAY")
    println(part1(input, 64))
    println(part2(input))
}
