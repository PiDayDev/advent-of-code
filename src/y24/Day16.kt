package y24

private const val DAY = "16"

fun main() {
    test()

    val input = readInput("Day$DAY")
    val pointsOfInterest = input.toPOIs()
    val maze = pointsOfInterest.toDeerMaze()

    val minDistance = maze.minDistanceFromStartToEnd
    println("PART 1: $minDistance")

    val niceSpots = countNiceSpots(maze, pointsOfInterest.road)
    println("PART 2: $niceSpots")
}

private data class PointsOfInterest(
    val start: Position,
    val end: Position,
    val poi: Set<Position>,
    val road: Set<Position>
) {
    private val validPositions: Set<Position> = road + start + end
    private val xRange = 0..1 + validPositions.maxOf { it.x }
    private val yRange = 0..1 + validPositions.maxOf { it.y }

    override fun toString(): String =
        yRange.joinToString("\n") { y ->
            xRange.joinToString("") { x ->
                when (Position(x, y)) {
                    start -> "S"
                    end -> "E"
                    in poi -> "*"
                    in road -> "."
                    else -> " "
                }
            }
        }

    fun toDeerMaze(): DeerMaze {
        val startNode = MazeNode(start, Direction.E)
        val endNodes = end.toMazeNodes().toSet()
        val pathNodes = (poi - end).flatMap(Position::toMazeNodes)
        val edges = mutableSetOf<MazeEdge>()
        // rotations
        val rotationCost = 1000
        pathNodes.forEach { node ->
            edges += MazeEdge(node, node.copy(direction = node.direction.turnRight()), rotationCost)
            edges += MazeEdge(node, node.copy(direction = node.direction.turnLeft()), rotationCost)
        }
        // translations
        val translationCost = 1
        pathNodes.forEach { node ->
            val (p, d) = node
            val line = generateSequence(p) { it + d }
                .drop(1)
                .takeWhile { it in validPositions }
                .toList()
            val idx = line.indexOfFirst { it == end || it in poi }
            if (idx >= 0) {
                val destination = line[idx]
                edges += MazeEdge(node, MazeNode(destination, d), (1 + idx) * translationCost)
            }
        }
        return DeerMaze(
            start = startNode,
            ends = endNodes,
            nodes = endNodes + pathNodes,
            edges = edges
        )
    }
}

private data class MazeNode(val position: Position, val direction: Direction)

private data class MazeEdge(val from: MazeNode, val to: MazeNode, val cost: Int) {
    fun invert() = MazeEdge(
        to = from.copy(direction = from.direction.opposite()),
        from = to.copy(direction = to.direction.opposite()),
        cost = cost
    )
}

private data class DeerMaze(
    val start: MazeNode,
    val ends: Set<MazeNode>,
    val nodes: Set<MazeNode>,
    val edges: Set<MazeEdge>
) {
    fun invertStartingOnDirection(direction: Direction) = DeerMaze(
        start = ends.first { it.direction == direction },
        ends = start.position.toMazeNodes().toSet(),
        nodes = nodes,
        edges = edges.map { it.invert() }.toSet(),
    )

    private val edgeMap: Map<MazeNode, List<MazeEdge>> = edges.groupBy { it.from }

    val minDistancesFromStart: Map<MazeNode, Long> by lazy {
        dijkstraMinDistancesFrom(start)
    }

    val minDistanceFromStartToEnd: Long by lazy {
        minDistancesFromStart
            .filterKeys { it in ends }
            .values
            .min()
    }

    private fun edgesFrom(node: MazeNode) = edgeMap[node] ?: emptyList()

    private fun dijkstraMinDistancesFrom(source: MazeNode): Map<MazeNode, Long> {
        val distances = mutableMapOf(source to 0L)
        val previous = mutableMapOf<MazeNode, MazeNode>()
        val queue = nodes.toMutableSet()
        while (queue.isNotEmpty()) {
            val next = distances.filterKeys { it in queue }.minByOrNull { it.value }
            if (next == null) break
            val (node, totalCostToNode) = next
            queue -= node

            edgesFrom(node).forEach { edge ->
                val (_, destination, cost) = edge
                val destinationCost = totalCostToNode + cost
                if (destinationCost < (distances[destination] ?: Long.MAX_VALUE)) {
                    distances[destination] = destinationCost
                    previous[destination] = node
                }
            }
        }
        return distances
    }
}

private fun List<String>.toPOIs(): PointsOfInterest {
    val x = classifyByChar()
    val start = x['S']!!.first()
    val end = x['E']!!.first()
    val road = x['.']!!.toSet()
    val points = mutableSetOf(start, end)
    road.forEach { p ->
        val ns = listOf(p + Direction.N, p + Direction.S).count { it in points || it in road }
        val we = listOf(p + Direction.W, p + Direction.E).count { it in points || it in road }
        // mostly cares about turning points => exclude straight corridors and dead ends
        val isVerticalCorridor = ns == 2 && we == 0
        val isHorizontalCorridor = ns == 0 && we == 2
        val isDeadEnd = ns + we <= 1
        if (!isVerticalCorridor && !isHorizontalCorridor && !isDeadEnd)
            points += p
    }
    return PointsOfInterest(start = start, end = end, poi = points, road = road)
}

private fun Position.toMazeNodes() = Direction.values()
    .map { MazeNode(position = this, direction = it) }

private fun countNiceSpots(maze: DeerMaze, road: Set<Position>): Int {
    val minDistance = maze.minDistanceFromStartToEnd

    val bestDistancesFromStart = maze.minDistancesFromStart

    val bestDistancesToEnd = mutableMapOf<MazeNode, Long>()
    Direction.values().forEach { dir ->
        val minDistances = maze.invertStartingOnDirection(dir).minDistancesFromStart
        minDistances.forEach { (node, distance) ->
            bestDistancesToEnd[node] = distance.coerceAtMost(bestDistancesToEnd[node] ?: distance)
        }
    }

    val minCostPOIs: List<Position> = bestDistancesFromStart
        .keys
        .filter { node -> bestDistancesFromStart[node]!! + bestDistancesToEnd[node]!! <= minDistance }
        .map { it.position }

    val spots: MutableSet<Position> = minCostPOIs.toMutableSet()
    val walkable: Set<Position> = road + minCostPOIs
    for (j in minCostPOIs.indices) {
        for (k in j + 1..minCostPOIs.lastIndex) {
            val p1 = minCostPOIs[j]
            val p2 = minCostPOIs[k]
            if (p1.x != p2.x && p1.y != p2.y) continue

            val range = p1..p2
            if (range.all { it in walkable })
                spots += range
        }
    }
    return spots.size
}

private fun test() {
    // test if implementation meets criteria from the description
    val testPOIs = """
         ###############
         #.......#....E#
         #.#.###.#.###.#
         #.....#.#...#.#
         #.###.#####.#.#
         #.#.#.......#.#
         #.#.#####.###.#
         #...........#.#
         ###.#.#####.#.#
         #...#.....#.#.#
         #.#.#.###.#.#.#
         #.....#...#.#.#
         #.###.#.#.#.#.#
         #S..#.....#...#
         ###############
     """.trimIndent()
        .lines().toPOIs()
    val testMaze = testPOIs.toDeerMaze()
    val t1 = testMaze.minDistanceFromStartToEnd
    println("TEST 1: $t1")
    check(t1 == 7036L)
    val t2 = countNiceSpots(testMaze, testPOIs.road)
    println("TEST 2: $t2")
    check(t2 == 45)
}
