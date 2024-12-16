package y24

private const val DAY = "16"

fun main() {
    fun part1(input: List<String>): Long {
        val pointsOfInterest = input.toPOIs()
//        println(pointsOfInterest)
        val maze = pointsOfInterest.toDeerMaze()
//        println(maze)
        return maze.minDistanceFromStartToEnd()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = """
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
         """.trimIndent().lines()
    val t1 = part1(testInput)
    println("TEST 1: $t1")
    check(t1 == 7036L)

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
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

private fun List<String>.toPOIs(): PointsOfInterest {
    val x = classifyByChar()
    val start = x['S']!!.first()
    val end = x['E']!!.first()
    val road = x['.']!!.toSet()
    // mostly cares about turning points => exclude points with space exactly on two opposite sides
    val points = mutableSetOf(start, end)
    road.forEach { p ->
        val ns = listOf(p + Direction.N, p + Direction.S).count { it in points || it in road }
        val we = listOf(p + Direction.W, p + Direction.E).count { it in points || it in road }
        if (!(ns == 2 && we == 0) && !(ns == 0 && we == 2))
            points += p
    }
    return PointsOfInterest(start = start, end = end, poi = points, road = road)
}

private data class MazeNode(val position: Position, val direction: Direction) {
    override fun toString(): String = "$position$direction"
}

private fun Position.toMazeNodes() = Direction.values().map { MazeNode(position = this, direction = it) }

private data class MazeEdge(val from: MazeNode, val to: MazeNode, val cost: Int) {
    override fun toString(): String = "$from--[$cost]-->$to"
}

private data class DeerMaze(
    val start: MazeNode,
    val ends: Set<MazeNode>,
    val nodes: Set<MazeNode>,
    val edges: Set<MazeEdge>
) {
    fun edgesFrom(node: MazeNode) = edges.filter { it.from == node }

    fun minDistanceFromStartToEnd(): Long =
        dijkstraMinPaths(start)
            .filterKeys { it in ends }
            .values
            .min()
    }

    private fun dijkstraMinPaths(source: MazeNode): Map<MazeNode, Long> {
        val dist = mutableMapOf<MazeNode, Long>()
        val prev = mutableMapOf<MazeNode, MazeNode>()
        val queue = nodes.toMutableSet()
        dist[source] = 0L
        while (queue.isNotEmpty()) {
            val u = queue.minBy { dist[it] ?: Long.MAX_VALUE }
            queue -= u
            val d = dist[u] ?: break

            edgesFrom(u).forEach { edge ->
                val (_, v, cost) = edge
                val alt = d + cost
                if (alt < (dist[v] ?: Long.MAX_VALUE)) {
                    dist[v] = alt
                    prev[v] = u
                }
            }
        }
        return dist
    }

    override fun toString(): String {
        val b = StringBuilder()
        b.appendLine("START = $start")
        ends.forEachIndexed { index, end ->
            b.appendLine("END#$index = $end")
        }
        edges.forEach { if (it.cost != 1000) b.appendLine(it) }
        return b.toString()
    }
}
