package y24

private const val DAY = "18"

fun main() {

    val input = readInput("Day$DAY").toBytes()
    val start = Position(0, 0)
    val end = Position(70, 70)

    fun Position.isValid() = x in start.x..end.x && y in start.y..end.y

    fun shortestPath(from: Position, to: Position, obstacles: Set<Position>): Int? {
        val edge = mutableSetOf(from)
        val visited = mutableSetOf<Position>()

        generateSequence(0) { it + 1 }.forEach { distance ->
            if (to in edge) return distance
            visited += edge
            val newEdge = edge
                .asSequence()
                .flatMap { it.around() }
                .distinct()
                .filter { it.isValid() }
                .filter { it !in visited && it !in obstacles }
                .toSet()
            edge.clear()
            edge += newEdge
            if (edge.isEmpty()) return null
        }
        return null
    }

    fun part2(input: List<Position>): String {
        val lockTime = generateSequence(1025) { it + 1 }.first { time ->
            null == shortestPath(start, end, input.take(time).toSet())
        }
        val obstacle = input.take(lockTime).last()
        return "${obstacle.x},${obstacle.y}"
    }

    println(shortestPath(start, end, input.take(1024).toSet()))
    println(part2(input))
}

private fun List<String>.toBytes(): List<Position> =
    map { it.split(",") }.map { (x, y) -> Position(x.toInt(), y.toInt()) }
