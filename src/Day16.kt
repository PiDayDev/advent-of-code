private const val DAY = 16

private data class Valve(val id: String, val rate: Int = 0, val neighbors: List<String>)

private fun String.toValve(): Valve {
    val id = substringAfter("Valve ").substringBefore(" has flow")
    val rate = substringAfter("rate=").substringBefore(";")
    val next = substringAfter("valve ").substringAfter("valves ").split(", ")
    return Valve(id, rate.toInt(), next)
}

private class ValveGraph(
    val arcsWithCosts: Map<String, Map<String, Int>>,
    val nodesWithValues: Map<String, Int>
) {
    override fun toString() = """ValveGraph $nodesWithValues
${arcsWithCosts.toList().joinToString(separator = "\n") { "${it.first} -> ${it.second}" }}"""

    fun maximizeValue(totalTime: Int): Int = maximizeValueRecursive(totalTime, "AA", emptyList(), 0, 0)

    private fun maximizeValueRecursive(totalTime: Int, from: String, open: List<String>, costSoFar: Int, valueSoFar: Int): Int {
        val candidates = arcsWithCosts[from]!!.filterKeys { it !in open }

        if (candidates.isEmpty())
            return valueSoFar

        return candidates.maxOf { (candidate, cost) ->
            val openAt = costSoFar + cost + 1
            if (openAt < totalTime) {
                val deltaValue = (totalTime - openAt) * nodesWithValues[candidate]!!
                maximizeValueRecursive(totalTime, candidate, open + candidate, openAt, valueSoFar + deltaValue)
            } else {
                valueSoFar
            }
        }
    }

    fun restrictToIds(ids: Set<String>): ValveGraph {
        val allIds = ids + "AA"
        val arcs = arcsWithCosts
            .filterKeys { it in allIds }
            .mapValues { (_, map) -> map.filterKeys { it in ids } }
        val nodes = nodesWithValues.filterKeys { it in ids }
        return ValveGraph(arcs, nodes)
    }
}

fun main() {
    fun minDistances(valveMap: Map<String, Valve>, from: String): Map<String, Int> {
        val distances = mutableMapOf(from to 0)
        while (distances.size < valveMap.size) {
            val tmp = mutableMapOf<String, Int>()
            distances.forEach { (id, dist) ->
                val neighbors = valveMap[id]!!.neighbors
                neighbors.forEach { n ->
                    val curr = distances[n]
                    if (curr == null || curr > dist + 1)
                        tmp[n] = dist + 1
                }
            }
            distances += tmp
        }
        return distances.filterKeys { valveMap[it]!!.rate > 0 }
    }

    fun Map<String, Valve>.toValveGraph(): ValveGraph {
        val arcs = keys.mapNotNull { k ->
            when {
                k == "AA" || this[k]!!.rate > 0 -> k to minDistances(this, k)
                else -> null
            }
        }.toMap()
        return ValveGraph(
            arcsWithCosts = arcs,
            nodesWithValues = mapValues { (_, v) -> v.rate }.filterValues { it > 0 }
        )
    }

    fun part1(graph: ValveGraph) = graph.maximizeValue(30)

    fun part2(graph: ValveGraph): Int {
        val nodeCount = graph.nodesWithValues.size
        val nodeIds = graph.nodesWithValues.keys.toList()
        return (0 until (1 shl nodeCount - 1)).maxOf { bitmask ->
            val myNodes = nodeIds.filterIndexed { index, _ -> (1 shl index).and(bitmask) == 0 }.toSet()
            val myGraph = graph.restrictToIds(myNodes)
            val eleGraph = graph.restrictToIds(nodeIds.toSet() - myNodes)
            val myValue = myGraph.maximizeValue(26)
            val eleValue = eleGraph.maximizeValue(26)
            myValue + eleValue
        }
    }

    val input = readInput("Day${DAY}")
    val valveGraph = input.map(String::toValve).associateBy { it.id }.toValveGraph()

    println(part1(valveGraph))
    println(part2(valveGraph))
}

