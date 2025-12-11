package y25

import java.util.*

private const val DAY = "11"

private typealias Node = String

private class Graph {
    val nodes = mutableSetOf<Node>()
    val edges = mutableMapOf<Node, List<Node>>()
    val topoOrder = mutableListOf<Node>()

    fun add(rawEdges: String) {
        val (source, dst) = rawEdges.split(":")
        val destinations = dst.trim().split(" ")
        nodes += source
        nodes += destinations
        edges[source] = destinations
    }

    fun topologicalSort() {
        topoOrder.clear()

        val indegree = mutableMapOf<Node, Int>()
        edges.values.flatten().forEach {
            indegree[it] = 1 + (indegree[it] ?: 0)
        }

        // Perform topological sort using Kahn's algorithm
        val nodesWithIncoming = indegree.filterValues { it > 0 }.keys
        val q: Queue<Node> = LinkedList(nodes - nodesWithIncoming)

        while (!q.isEmpty()) {
            val node = q.poll()!!
            topoOrder += node

            val neighbors = edges[node] ?: emptyList()
            for (neighbor in neighbors) {
                indegree[neighbor] = indegree[neighbor]?.minus(1) ?: 0
                if (indegree[neighbor] == 0) {
                    q += neighbor
                }
            }
        }
    }

    fun countPaths(source: Node, destination: Node): Long {
        if (topoOrder.isEmpty())
            topologicalSort()

        // Array to store number of ways to reach each node
        val ways = mutableMapOf<Node, Long>()
        ways[source] = 1

        // Traverse in topological order
        for (node in topoOrder) {
            for (neighbor in edges[node] ?: emptyList()) {
                ways[neighbor] = (ways[neighbor] ?: 0) + (ways[node] ?: 0)
            }
        }

        return ways[destination] ?: 0
    }

    enum class Visited {
        NONE, DAC, FFT, BOTH
    }

    fun countPathsWithSpecialNodes(source: Node, destination: Node): Map<Visited, Long> {
        if (topoOrder.isEmpty())
            topologicalSort()

        // Array to store number of ways to reach each node
        val ways = mutableMapOf<Node, Map<Visited, Long>>()
        ways[source] = mapOf(Visited.NONE to 1)

        // Traverse in topological order
        for (node in topoOrder) {
            for (neighbor in edges[node] ?: emptyList()) {
                val prev = ways[neighbor] ?: emptyMap()
                val next = ways[node] ?: emptyMap()
                val added: Map<Visited, Long> = when (neighbor) {
                    "dac" -> mapOf(
                        Visited.DAC to (next[Visited.NONE] ?: 0) + (next[Visited.DAC] ?: 0),
                        Visited.BOTH to (next[Visited.FFT] ?: 0) + (next[Visited.BOTH] ?: 0),
                    )

                    "fft" -> mapOf(
                        Visited.FFT to (next[Visited.NONE] ?: 0) + (next[Visited.FFT] ?: 0),
                        Visited.BOTH to (next[Visited.DAC] ?: 0) + (next[Visited.BOTH] ?: 0),
                    )

                    else -> next
                }
                ways[neighbor] = mutableMapOf<Visited, Long>().also { union ->
                    Visited.values().forEach {
                        union[it] = (prev[it] ?: 0) + (added[it] ?: 0)
                    }
                }
            }
        }

        return ways[destination] ?: emptyMap()
    }
}

fun main() {

    fun part1(graph: Graph) =
        graph.countPaths("you", "out")

    fun part2(graph: Graph) =
        graph.countPathsWithSpecialNodes("svr", "out")[Graph.Visited.BOTH]

    val graph = Graph()
    readInput("Day$DAY").forEach { graph.add(it) }

    println(part1(graph))
    println(part2(graph))
}
