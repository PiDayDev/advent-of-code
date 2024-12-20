package y24

private const val DAY = "20"

fun main() {
    test()

    val track = readInput("Day$DAY").toRaceTrack()
    println(track)

    val part1 = track.countCheats(minSaved = 100, maxSize = 2)
    println("PART 1: $part1")
    check(part1 == 1452)

    val part2 = track.countCheats(minSaved = 100, maxSize = 20)
    println("PART 2: $part2")
    check(part2 == 999556)
}

private fun List<String>.toRaceTrack(): RaceTrack {
    val map = classifyByChar()
    val start = map['S']!!.first()
    val end = map['E']!!.first()
    val roadTiles = map['.']!!.toSet()
    return RaceTrack(start, end, roadTiles)
}

private class RaceTrack(val start: Position, val end: Position, private val roadTiles: Set<Position>) {

    private val road: List<Position> by lazy {
        val path = mutableListOf(start)
        while (true) {
            val last = path.last()
            if (last == end) break
            val next = last.around()
                .filter { it in roadTiles || it == end }
                .first { path.size < 2 || path[path.lastIndex - 1] != it }
            path += next
        }
        path
    }

    /* Cached for performance reason */
    private val tileToRoadIndex: Map<Position, Int> by lazy {
        road.mapIndexed { index, position -> position to index }.toMap()
    }

    private val xRange: IntRange by lazy { road.map { it.x }.sorted().let { it.first()..it.last() } }
    private val yRange: IntRange by lazy { road.map { it.y }.sorted().let { it.first()..it.last() } }

    fun countCheats(minSaved: Int, maxSize: Int): Int = road.sumOf {
        cheatsWithSavingFrom(it, maxSize)
            .count { (_, saved) -> saved >= minSaved }
    }

    private fun cheatsWithSavingFrom(position: Position, maxSize: Int): Map<Cheat, Int> {
        val index = tileToRoadIndex[position]!!

        val destinationsToDistance: Map<Position, Int> = road
            .drop(index + 1)
            .associateWith { d -> position manhattanDistance d }
            .filterValues { it <= maxSize }

        val result = mutableMapOf<Cheat, Int>()
        destinationsToDistance.forEach { (tile, distance) ->
            val jumpSize = (tileToRoadIndex[tile] ?: -1) - index - distance
            if (jumpSize > 0)
                result[Cheat(position, tile)] = jumpSize
        }
        return result
    }

    override fun toString(): String =
        yRange.joinToString("\n") { y ->
            xRange.joinToString("") { x ->
                when (Position(x, y)) {
                    start -> "S"
                    end -> "E"
                    in roadTiles -> "."
                    else -> " "
                }
            }
        }

}

private data class Cheat(val start: Position, val end: Position)

private fun test() {
    val testTrack = """
        ###############
        #...#...#.....#
        #.#.#.#.#.###.#
        #S#...#.#.#...#
        #######.#.#.###
        #######.#.#...#
        #######.#.###.#
        ###..E#...#...#
        ###.#######.###
        #...###...#...#
        #.#####.#.###.#
        #.#...#.#.#...#
        #.#.#.#.#.#.###
        #...#...#...###
        ###############
    """.trimIndent().lines().toRaceTrack()

    val test1 = testTrack.countCheats(minSaved = 2, maxSize = 2)
    //println("TEST 1: $test1")
    val expectedTest1 = 14 + 14 + 2 + 4 + 2 + 3 + 1 * 5
    check(test1 == expectedTest1) { "$test1 != $expectedTest1" }

    val test2 = testTrack.countCheats(minSaved = 50, maxSize = 20)
    //println("TEST 2: $test2")
    val expectedTest2 = 32 + 31 + 29 + 39 + 25 + 23 + 20 + 19 + 12 + 14 + 12 + 22 + 4 + 3
    check(test2 == expectedTest2) { "$test2 != $expectedTest2" }
}

