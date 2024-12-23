package y24

private const val DAY = "23"

fun main() {
    fun part1(party: LanParty): Int = party
        .computers
        .filter { it.startsWith("t") }
        .flatMapTo(mutableSetOf()) { party.trianglesContaining(it) }
        .size

    fun part2(party: LanParty): String {
        val computerToCoupling: Map<Computer, Int> = party.computers.associateWith { party.heuristicCouplingScore(it) }

        val maxCoupling = computerToCoupling.values.max()

        val probableClique = computerToCoupling.filterValues { it == maxCoupling }.keys

        // check it's actually a clique
        probableClique.forEach { a ->
            check((probableClique - a).all { b -> party.areNeighbors(a, b) })
        }

        // ok, that was lucky
        return probableClique.sorted().joinToString(",")
    }

    val party = readInput("Day$DAY").toParty()

    val p1 = part1(party)
    println("PART 1: $p1")
    check(p1 == 1230)

    val p2 = part2(party)
    println("PART 2: $p2")
    check(p2 == "az,cj,kp,lm,lt,nj,rf,rx,sn,ty,ui,wp,zo")
}

private typealias Computer = String
private typealias Clique = Set<Computer>

private data class LanParty(
    val computers: Set<Computer>,
    private val connections: Map<Computer, Set<Computer>>
) {
    fun neighborsOf(computer: Computer) = connections[computer]!!

    fun areNeighbors(a: Computer, b: Computer) = a in neighborsOf(b)

    fun trianglesContaining(computer: Computer): Set<Clique> {
        val neighbors = neighborsOf(computer).toList()
        val cliques = mutableSetOf<Clique>()
        neighbors.forEach { peer1 ->
            neighbors.forEach { peer2 -> // non-optimized loop, easier to read
                if (areNeighbors(peer1, peer2))
                    cliques += setOf(computer, peer1, peer2)
            }
        }
        return cliques
    }

    /** I define a "coupling score" of a PC as the sum on its neighbors of the count of mutual neighbors */
    fun heuristicCouplingScore(computer: Computer): Int {
        val neighbors = neighborsOf(computer)
        return neighbors.sumOf { peer ->
            val mutuals = neighborsOf(peer).intersect(neighbors)
            mutuals.size
        }
    }
}

private fun List<String>.toParty(): LanParty {
    val computers = mutableSetOf<Computer>()
    val connections = mutableMapOf<Computer, Set<Computer>>()
    forEach { s ->
        val nodes = s.split("-")
        computers += nodes
        val (a, b) = nodes
        connections.merge(a, setOf(b), Set<Computer>::plus)
        connections.merge(b, setOf(a), Set<Computer>::plus)
    }
    return LanParty(computers, connections)
}

















