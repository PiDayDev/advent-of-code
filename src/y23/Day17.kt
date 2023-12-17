package y23

private const val DAY = "17"

data class Node(val position: Position, val direction: Dir)

fun Dir.turningDirections() = when (this) {
    Dir.N, Dir.S -> listOf(Dir.E, Dir.W)
    Dir.E, Dir.W -> listOf(Dir.N, Dir.S)
}

const val MAX_COST = 10_000_000

fun main() {

    fun minHeatLoss(heatLossByPosition: Map<Position, Int>, validLengths: IntRange): Int {
        val coordinates = heatLossByPosition.keys
        val start = Position(0, 0)
        val goal = Position(coordinates.maxOf { it.x }, coordinates.maxOf { it.y })

        val finalizedCosts = mutableMapOf<Node, Int>()
        val temporaryCosts = mutableMapOf(
            Node(start, Dir.S) to 0
        )

        while (temporaryCosts.isNotEmpty()) {
            val (node, cost) = temporaryCosts.toList().minByOrNull { (_, v) -> v }!!
            temporaryCosts -= node
            finalizedCosts[node] = cost

            val nextDirections = node.direction.turningDirections()
            nextDirections.forEach { dir ->
                val positions: List<Position> = (1..validLengths.last)
                    .runningFold(node.position) { curr, _ -> curr + dir.movement }
                    .drop(1)

                val stepCosts = positions.map { heatLossByPosition[it] ?: MAX_COST }
                val nodes = positions.map { position -> Node(position, dir) }

                val costs = stepCosts.runningFold(cost, Int::plus).drop(1)

                nodes.forEachIndexed { j, node ->
                    val pathLength = j + 1
                    if (pathLength in validLengths) {
                        val c = costs[j]
                        if (node !in finalizedCosts && c < (temporaryCosts[node] ?: MAX_COST)) {
                            temporaryCosts[node] = c
                        }
                    }
                }
            }
        }

        return finalizedCosts.filterKeys { node -> node.position == goal }.values.minOrNull()!!
    }

    fun part1(heatLossByPosition: Map<Position, Int>) = minHeatLoss(heatLossByPosition, 1..3)

    fun part2(heatLossByPosition: Map<Position, Int>) = minHeatLoss(heatLossByPosition, 4..10)
    
    val input = readInput("Day$DAY")

    val heatLossByPosition: Map<Position, Int> = input
        .flatMapIndexed { y, row ->
            row.mapIndexed { x, c ->
                val cost = c.digitToInt()
                Position(x, y) to cost
            }
        }
        .toMap()

    println(part1(heatLossByPosition))
    println(part2(heatLossByPosition))
}
