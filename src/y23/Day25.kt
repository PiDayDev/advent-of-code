package y23

private const val DAY = "25"

private const val nodeDelimiter = "+"

fun main() {
    fun part1(input: List<String>): Long {
        val fullGraph = toGraph(input)

        /* See https://en.wikipedia.org/wiki/Karger%27s_algorithm */
        repeat(Int.MAX_VALUE) { ex ->
            var graph = fullGraph
            while (graph.nodes.size > 2) {
                val (node1, node2) = graph.edges.random()
                graph = graph.shrink(setOf(node1, node2))
            }
            val cutValue = graph.edges.sumOf { it.weight }
            println("Execution #$ex => cut value = $cutValue")
            if (cutValue == 3) {
                val (n1, n2) = graph.nodes
                return n1.split(nodeDelimiter).size.toLong() * n2.split(nodeDelimiter).size.toLong()
            }
        }
        return -1L
    }

    val input = readInput("Day$DAY")
    println(part1(input))
}

private data class Edge(val node1: String, val node2: String, val weight: Int)

private data class Graph(val nodes: List<String>, val edges: List<Edge>) {

    fun shrink(joined: Set<String>): Graph {
        val newNode = joined.joinToString(nodeDelimiter)
        val oldNodes = nodes - joined

        val (toChange, unchanged) = edges.partition { edge -> edge.node1 in joined || edge.node2 in joined }
        val map = mutableMapOf<String, Int>()
        toChange.forEach { (m, n, w) ->
            if (m in joined && n !in joined) map[n] = w + map.getOrPut(n) { 0 }
            if (n in joined && m !in joined) map[m] = w + map.getOrPut(m) { 0 }
        }
        val changed = map.map { (node, weight) -> Edge(newNode, node, weight) }

        return copy(nodes = oldNodes + newNode, edges = changed + unchanged)
    }

}

private fun toGraph(input: List<String>): Graph {
    val nodes = mutableSetOf<String>()
    val edges = mutableListOf<Edge>()
    input.forEach {
        val src = it.substringBefore(":")
        nodes.add(src)
        val destinations = it.substringAfter(":").split(" ").filter(String::isNotBlank)
        destinations.forEach { dst ->
            nodes.add(dst)
            edges.add(Edge(src, dst, 1))
        }
    }
    return Graph(nodes.toList(), edges)
}

