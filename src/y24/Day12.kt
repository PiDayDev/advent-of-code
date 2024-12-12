package y24

private const val DAY = "12"

fun main() {
    val regions = readInput("Day$DAY").toRegions()
    println(regions.sumOf { it.area * it.perimeter })
    println(regions.sumOf { it.area * it.sides })
}

private fun List<String>.toRegions(): List<Region> {
    fun garden(): Map<Position, Char> {
        val map = mutableMapOf<Position, Char>()
        forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                map[Position(x, y)] = c
            }
        }
        return map
    }

    fun Map<Position, Char>.toRegions(): List<Region> {
        val garden = toMutableMap()
        val regions = mutableListOf<Region>()
        while (garden.isNotEmpty()) {
            // choose a plot
            val (position, plant) = garden.iterator().next()
            // start a region
            val region = mutableSetOf<Position>()
            // flood fill the region
            generateSequence(setOf(position)) { last ->
                region.addAll(last)
                last.flatMap { it.around() }
                    .filter { it !in region && garden[it] == plant }
                    .toSet()
            }
                .takeWhile { it.isNotEmpty() }
                .count()
            // remove the region from the garden
            garden -= region
            regions += Region(region.toSet())
        }
        return regions
    }

    return garden().toRegions()
}


private data class Region(val plots: Set<Position>) {
    val area = plots.size

    val perimeter: Int by lazy {
        plots.sumOf { p ->
            p.around().filter { it !in plots }.size
        }
    }

    val sides: Int by lazy {
        // clockwise - internal edges are inserted twice, once in each direction
        val edges = plots.flatMap { it.clockwiseSides() }.toSet()
        // counterclockwise - will still contain each duplicated edge
        val opposites = edges.map { it.second to it.first }.toSet()
        //  ...so this gets rid of the duplicates
        val externalEdges = edges - opposites

        externalEdges.countLines()
    }

}

private typealias Segment = Pair<Position, Position>

private fun Position.clockwiseSides(): List<Segment> {
    val a = this
    val b = copy(x = x + 1)
    val c = copy(x = x + 1, y = y + 1)
    val d = copy(y = y + 1)
    return listOf(a to b, b to c, c to d, d to a)
}

private infix fun Segment.continues(that: Segment): Boolean {
    val (a, b) = this
    val (c, d) = that
    return (b == c || d == a) && b.x - a.x == d.x - c.x && b.y - a.y == d.y - c.y
}

private fun Collection<Segment>.countLines(): Int {
    val rest = toSet().toMutableSet()
    var count = 0
    while (rest.isNotEmpty()) {
        val line = mutableSetOf<Segment>()
        var current: Segment? = rest.first()
        while (current != null) {
            line += current
            rest -= current
            current = rest.firstOrNull { candidate ->
                line.any { candidate continues it }
            }
        }
        count++
    }
    return count
}