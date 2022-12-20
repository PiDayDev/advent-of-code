private const val DAY = 17

private typealias Rock = List<Point2D>

private val rocks: List<Rock> = listOf(
    /* - */ listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0),
    /* + */ listOf(1 to 0, 1 to 2, 0 to 1, 1 to 1, 2 to 1),
    /* _|*/ listOf(0 to 0, 1 to 0, 2 to 0, 2 to 1, 2 to 2),
    /* | */ listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3),
    /* # */ listOf(0 to 0, 1 to 0, 0 to 1, 1 to 1),
)

private fun Rock.left() = when {
    any { it.x <= 0 } -> this
    else -> map { (x, y) -> x - 1 to y }
}

private fun Rock.right() = when {
    any { it.x >= 6 } -> this
    else -> map { (x, y) -> x + 1 to y }
}

private fun Rock.vertical(n: Int) = map { (x, y) -> x to y + n }

private class Cave17(val jets: String) {
    private val stable = mutableListOf<Point2D>()
    private var falling: Rock = emptyList()
    private var jetIndex: Int = 0

    fun jetty() = jetIndex

    fun maxY() = stable.maxOfOrNull { it.y } ?: -1

    fun appear(rock: Rock) {
        val rockX = rock.right().right()
        falling = rockX.vertical(maxY() + 4)
    }

    fun jetsPushRock() {
        val direction = jets[jetIndex]
        jetIndex = (1 + jetIndex) % jets.length
        val candidateRock = when (direction) {
            '>' -> falling.right()
            '<' -> falling.left()
            else -> falling
        }
        if (candidateRock - stable.toSet() == candidateRock) {
            falling = candidateRock
        }
    }

    /** @return true if floor was reached */
    fun rockFalls(): Boolean {
        val candidateRock = falling.vertical(-1)
        val collides = candidateRock - stable.toSet() != candidateRock
                || candidateRock.any { it.y < 0 }
        falling = if (collides) {
            stable.addAll(falling)
            emptyList()
        } else {
            candidateRock
        }
        return collides
    }

    override fun toString(): String {
        val s = StringBuilder()
        (maxY() + 6 downTo 0).forEach { y ->
            s.append("|")
            (0..6).forEach { x ->
                val pos = x to y
                s.append(
                    when (pos) {
                        in falling -> "@"
                        in stable -> "#"
                        else -> "."
                    }
                )
            }
            s.append("| > $y\n")
        }
        s.append("+-------+")
        return s.toString()
    }

    fun simulate(rockCount: Int) {
        repeat(rockCount) { rockIndex ->
            val rock = rocks[rockIndex % rocks.size]
            appear(rock)
            var stopped = false
            while (!stopped) {
                jetsPushRock()
                stopped = rockFalls()
            }
            if (rockIndex % 100 == 0)
                println("Rock $rockIndex - Jet index $jetIndex - Y ${maxY()}  ")
        }
    }

    fun rows() = stable
        .groupBy(keySelector = { it.y }, valueTransform = { it.x })
        .toSortedMap()
        .toList()
        .map { it.second.sorted() }
}

fun main() {

    fun part1(jets: String): Int {
        val cave = Cave17(jets)
        cave.simulate(2022)
        return cave.maxY() + 1
    }

    fun part2_manually(jets: String): Long {
        val cave = Cave17(jets)
        val totalRocks = 1_000_000_000_000L

        // FIXME deduced from logs, could be done programmatically
        val rocksInPeriod = 1700

        val mod = (totalRocks % rocksInPeriod).toInt()
        val initialRockCount = mod + rocksInPeriod * 3
        cave.simulate(initialRockCount)

        println("-----------------------\n- SIMULATION COMPLETE -")

        val rows = cave.rows()

        // FIXME deduced from logs, could be done programmatically
        val rowsInPeriod = 2623

        val remainingRockCount = totalRocks - initialRockCount
        val remainingPeriods = remainingRockCount / rocksInPeriod
        val additionalRows = remainingPeriods * rowsInPeriod
        val totalRows = additionalRows + rows.size

        println(
            """
            Total rows = ${rows.size}.
            The last $rowsInPeriod rows will repeat periodically.. it takes $rocksInPeriod rocks to fill $rowsInPeriod rows.
            - How many rocks should still fall? $remainingRockCount
            - How many periods should still be repeated? $remainingPeriods
            - How many rows will be added? $additionalRows
            - How many rows will there be in total? $totalRows
            """.trimIndent()
        )

        return totalRows
    }

    val input = readInput("Day${DAY}").first()
    println(part1(input))
    println(part2_manually(input))
}
