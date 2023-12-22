package y23

private const val DAY = "21"

fun main() {
    fun Int.isEven() = this and 1 == 0
    fun Int.isOdd() = this and 1 > 0

    fun dijkstra(input: List<String>, start: Position, max: Int): Map<Position, Int> {
        val distances = mutableMapOf<Position, Int>()
        var frontier = setOf(start)
        var distance = 0

        while (frontier.isNotEmpty() && distance <= max) {
            frontier.forEach { distances[it] = distance }
            frontier = frontier
                .asSequence()
                .flatMap { it.around() }
                .filterNot { it in distances }
                .filter { (x, y) -> (input.getOrNull(y)?.getOrNull(x) ?: '#') in "S." }
                .toSet()
            distance++
        }
        return distances
    }

    fun part1(input: List<String>, goal: Int): Int {
        val yStart = input.indexOfFirst { 'S' in it }
        val xStart = input[yStart].indexOf('S')

        val distances = dijkstra(input, Position(xStart, yStart), goal)

        return distances.values.count { it % 2 == goal % 2 }
    }

    fun part2(input: List<String>, steps: Int): Long {
        val xMin = 0
        val xMax = input.first().lastIndex

        val yMin = 0
        val yMax = input.lastIndex

        val rowStart = input.first { 'S' in it }
        val xStart = rowStart.indexOf('S')
        val yStart = input.indexOf(rowStart)

        val distances = dijkstra(input, Position(xStart, yStart), xMax + yMax)

        val evenTot = distances.values.count(Int::isEven)
        val oddTot = distances.values.count(Int::isOdd)

        val width = input.first().length

        val distanceCenterCorner = width - 1
        val distanceCenterBorder = distanceCenterCorner / 2

        // total squares in horizontal from center to spike
        val tsqh = (steps - distanceCenterBorder) / width

        val squaresLikeStart = (tsqh - 1).toLong().let { it * it }
        val squaresAlternate = tsqh.toLong().let { it * it }

        val spikesCount = 1
        val smallCornersCount = tsqh
        val largeCornersCount = tsqh - 1

        val residualDistForLargeCorner = distanceCenterBorder + distanceCenterCorner
        val residualDistForSmallCorner = distanceCenterBorder - 1

        println(
            """
            vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
            Distanza centro - bordo sulla board    $distanceCenterCorner
            Distanza centro - angolo sulla board   $distanceCenterBorder
            Larghezza == altezza                   $width
            Passi totali                           $steps
             
            Board totali in orizz.dal centro       $tsqh
            Board con scacchiera come inizio       $squaresLikeStart
            Board con scacchiera opposta           $squaresAlternate
             
            Punte                                  $spikesCount per ogni tipo
            Angoli interni (grandi)                $largeCornersCount per ogni tipo
            Angoli esterni (piccoli)               $smallCornersCount per ogni tipo
             
            Dist.residua punte                     $distanceCenterCorner
            Dist.residua angoli interni (grandi)   $residualDistForLargeCorner
            Dist.residua angoli esterni (piccoli)  $residualDistForSmallCorner
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        """.trimIndent()
        )

        fun Map<Position, Int>.spikeCount() = values.count { it.isEven() }
        val spikeN = dijkstra(input, Position(xStart, yMax), distanceCenterCorner).spikeCount()
        val spikeS = dijkstra(input, Position(xStart, yMin), distanceCenterCorner).spikeCount()
        val spikeW = dijkstra(input, Position(xMax, yStart), distanceCenterCorner).spikeCount()
        val spikeE = dijkstra(input, Position(xMin, yStart), distanceCenterCorner).spikeCount()

        fun Map<Position, Int>.largeCornerCount() = values.count { it.isOdd() }
        val largeNE = dijkstra(input, Position(xMin, yMax), residualDistForLargeCorner).largeCornerCount()
        val largeNW = dijkstra(input, Position(xMax, yMax), residualDistForLargeCorner).largeCornerCount()
        val largeSE = dijkstra(input, Position(xMin, yMin), residualDistForLargeCorner).largeCornerCount()
        val largeSW = dijkstra(input, Position(xMax, yMin), residualDistForLargeCorner).largeCornerCount()

        fun Map<Position, Int>.smallCornerCount() = values.count { it.isEven() }
        val smallNE = dijkstra(input, Position(xMin, yMax), residualDistForSmallCorner).smallCornerCount()
        val smallNW = dijkstra(input, Position(xMax, yMax), residualDistForSmallCorner).smallCornerCount()
        val smallSE = dijkstra(input, Position(xMin, yMin), residualDistForSmallCorner).smallCornerCount()
        val smallSW = dijkstra(input, Position(xMax, yMin), residualDistForSmallCorner).smallCornerCount()

        return oddTot.toLong() * squaresLikeStart +
                evenTot.toLong() * squaresAlternate +
                (spikeN + spikeS + spikeW + spikeE).toLong() +
                (largeNE + largeNW + largeSE + largeSW).toLong() * largeCornersCount +
                (smallNE + smallNW + smallSE + smallSW).toLong() * smallCornersCount
    }

    val input = readInput("Day$DAY")
    println(part1(input, 64))
    println(part2(input, 26_501_365))
}
