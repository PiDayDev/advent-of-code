package y23

private const val DAY = "23"

fun main() {

    fun toAreaMap(input: List<String>) = input
        .flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, c ->
                when (c) {
                    '#' -> null
                    else -> Position(x, y) to c
                }
            }
        }
        .toMap()

    fun validGridMovements(position: Position, tile: Char): Collection<Position> =
        when (tile) {
            '^' -> listOf(position + north)
            '>' -> listOf(position + east)
            'v' -> listOf(position + south)
            '<' -> listOf(position + west)
            else -> position.around()
        }

    fun Map<Position, Char>.longestGridPath(
        current: Position,
        goal: Position,
        visited: Set<Position>,
    ): Set<Position> {
        if (current == goal)
            return visited

        val steps = visited.toMutableSet()
        var validCandidates = listOf(current)
        while (validCandidates.size == 1) {
            val pos = validCandidates.single()
            steps += pos

            val currentTile = get(pos)!!
            val candidates = validGridMovements(pos, currentTile)
            validCandidates = (candidates - steps).filter { p -> p in this }
        }

        if (validCandidates.isEmpty() && goal in steps)
            return steps

        return validCandidates
            .asSequence()
            .map { next -> longestGridPath(next, goal, steps + next) }
            .maxByOrNull { it.size } ?: emptySet()
    }

    fun Map<Position, Char>.maxPathLength(): Int {
        val start = keys.minByOrNull { it.y }!!
        val goal = keys.maxByOrNull { it.y }!!
        val path = longestGridPath(start, goal, setOf(start))
        return (path - start).size
    }

    fun part1(input: List<String>): Int = toAreaMap(input).maxPathLength()

    var longestCost = -1
    var longest = emptySet<Position>()
    var counter = 0

    fun Map<Position, Map<Position, Int>>.longestGraphPath(
        current: Position,
        goal: Position,
        visited: Set<Position>,
        cost: Int
    ): Pair<Set<Position>, Int> {
        if (++counter % 1_000_000 == 0) println("$counter: longest path found = $longestCost")

        if (current == goal)
            return visited to cost

        val candidates: Map<Position, Int> = this[current]!! - visited
        if (candidates.isEmpty())
            return emptySet<Position>() to -1

        for ((next, w) in candidates) {
            val (p, c) = longestGraphPath(next, goal, visited + next, cost + w)
            if (c > longestCost) {
                longestCost = c
                longest = p
            }
        }
        return longest to longestCost
    }

    fun compress(adjacencyMatrix: MutableMap<Position, MutableMap<Position, Int>>) {
        val nodesWith2Neighbors = adjacencyMatrix.filterValues { it.size == 2 }.toList().toMutableList()
        while (nodesWith2Neighbors.isNotEmpty()) {
            val (k: Position, neighbors: MutableMap<Position, Int>) = nodesWith2Neighbors.removeFirst()
            val (left, right) = neighbors.toList()
            val (lNode, lCost) = left
            val (rNode, rCost) = right
            val newCost = lCost + rCost
            adjacencyMatrix[lNode]?.apply { put(rNode, newCost); remove(k) }
            adjacencyMatrix[rNode]?.apply { put(lNode, newCost); remove(k) }
            adjacencyMatrix.remove(k)
        }
    }

    fun part2(input: List<String>): Int {
        val positions = toAreaMap(input).keys
        val adjacencyMatrix: MutableMap<Position, MutableMap<Position, Int>> =
            positions
                .associateWith { it.around().filter { n -> n in positions }.associateWith { 1 }.toMutableMap() }
                .toMutableMap()

        compress(adjacencyMatrix)

        val (start, end) = adjacencyMatrix.filterValues { it.size == 1 }.keys.toList()
        val (_, cost) = adjacencyMatrix.longestGraphPath(start, end, setOf(start), 0)
        return cost
    }

    val input = readInput("Day$DAY")
    println("PART 1 = ${part1(input)}")
    println("PART 2 = ${part2(input)}")
}
