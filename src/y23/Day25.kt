package y23

private const val DAY = "25"

fun main() {
    fun part1(input: List<String>): Long {
        val graph = toGraph(input)
        val minimumCut = minimumCut(graph, graph.nodes.random())
        println("RESULT!! $minimumCut")

        val (size1, size2) = minimumCut.parts.toList()
            .map { collection -> collection.flatMap { it.split("+") }.toSet().size }
        println("$size1 * $size2 = ${size2 * size1}")
        return size1.toLong() * size2.toLong()
    }

    val input = readInput("Day$DAY")
    println(part1(input))
}

private data class Edge25(val node1: String, val node2: String, val weight: Int)

private data class Graph25(val nodes: List<String>, val edges: List<Edge25>) {
    private val n2es = mutableMapOf<String, MutableList<Edge25>>()

    init {
        edges.forEach { e ->
            n2es.getOrPut(e.node1) { mutableListOf() }.add(e)
            n2es.getOrPut(e.node2) { mutableListOf() }.add(e)
        }
    }

    fun edgesOf(v: String): List<Edge25> =
        n2es[v] ?: emptyList()

    fun weight(aSet: Set<String>, y: String): Int = edges
        .sumOf { (m, n, w) ->
            when {
                m == y && n in aSet -> w
                n == y && m in aSet -> w
                else -> 0
            }
        }

    fun shrink(joined: Set<String>): Graph25 {
        val newNode = joined.joinToString("+")
        val oldNodes = nodes - joined

        val (toChange, unchanged) = edges.partition { edge -> edge.node1 in joined || edge.node2 in joined }
        val map = mutableMapOf<String, Int>()
        toChange.forEach { (m, n, w) ->
            val mu = m in joined
            val nu = n in joined
            if (mu && !nu) map[n] = w + map.getOrPut(n) { 0 }
            if (nu && !mu) map[m] = w + map.getOrPut(m) { 0 }
        }
        val changed = map.map { (node, weight) -> Edge25(newNode, node, weight) }

        return copy(nodes = oldNodes + newNode, edges = changed + unchanged)
    }
}

private fun toGraph(input: List<String>): Graph25 {
    val nodes = mutableSetOf<String>()
    val edges = mutableListOf<Edge25>()
    input.forEach {
        val src = it.substringBefore(":")
        nodes.add(src)
        val destinations = it.substringAfter(":").split(" ").filter(String::isNotBlank)
        destinations.forEach { dst ->
            nodes.add(dst)
            edges.add(Edge25(src, dst, 1))
        }
    }
    return Graph25(nodes.toList(), edges)
}

/** See https://en.wikipedia.org/wiki/Stoer%E2%80%93Wagner_algorithm */
private fun minimumCut(g: Graph25, a: String): CutOfThePhase {
    (1..g.nodes.size).fold(g) { graph, _ ->
        val (newGraph, cutOfThePhase) = minimumCutPhase(graph, a)
        println("${newGraph.nodes.size} nodes to go | Cut of the phase: ${cutOfThePhase.value}")
        if (cutOfThePhase.value <= 3)
            return cutOfThePhase
        newGraph
    }
    throw IllegalStateException("Should never be here")
}

private data class CutOfThePhase(val parts: Pair<Collection<String>, Collection<String>>, val value: Int)

private fun minimumCutPhase(g: Graph25, a: String): Pair<Graph25, CutOfThePhase> {
    var result: CutOfThePhase? = null
    val aList = mutableListOf(a)

    val outside = g.nodes - a
    val weightOfConnectionsWithA: MutableMap<String, Int> =
        outside.associateWith { node -> g.weight(aList.toSet(), node) }.toMutableMap()
    val q: MutableList<String> = outside.toMutableList()

    while (aList.size < g.nodes.size) {
        q.sortByDescending { weightOfConnectionsWithA[it] ?: 0 }
        val z = q.removeFirst()
        val cost = weightOfConnectionsWithA[z]!!
        if (aList.size + 1 == g.nodes.size) {
            result = CutOfThePhase(setOf(z) to aList.toSet(), cost)
        }

        val toUpdate = g.edgesOf(z)
        toUpdate.forEach { (m, n, w) ->
            (listOf(m, n) - z).forEach { node ->
                when (val weight = weightOfConnectionsWithA[node]) {
                    is Int -> weightOfConnectionsWithA[node] = weight + w
                }
            }
        }
        weightOfConnectionsWithA.remove(z)
        aList.add(z)
    }

    return g.shrink(aList.takeLast(2).toSet()) to result!!
}
