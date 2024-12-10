package y24

private const val DAY = "10"

fun main() {
    val hikeMap = readInput("Day$DAY").toHikeMap()
    println(hikeMap.scorePart1())
    println(hikeMap.ratingPart2())
}

private typealias Trail = List<Position>

private const val MIN_HEIGHT = 0
private const val MAX_HEIGHT = 9

private data class HikeMap(private val topography: Map<Int, Set<Position>>) {
    fun scorePart1() = trailheads().sumOf { trailDestinations(it).size }

    fun ratingPart2() = trailheads().sumOf { trails(it).size }

    private fun trails(trailhead: Position): Set<Trail> {
        val initialTrail: Trail = listOf(trailhead)
        return (MIN_HEIGHT until MAX_HEIGHT).fold(setOf(initialTrail)) { trails, height ->
            trails
                .flatMap { trail ->
                    climb(height, trail.last()).map { next -> trail + next }
                }
                .toSet()
        }
    }

    private fun trailDestinations(trailhead: Position): Set<Position> =
        trails(trailhead).map { it.last() }.toSet()

    private fun trailheads() = topography[MIN_HEIGHT]!!

    private fun climb(height: Int, position: Position): Set<Position> {
        check(position in topography[height]!!)
        val availableDestinations = topography[height + 1] ?: emptySet()
        return position.around().filter { it in availableDestinations }.toSet()
    }
}

private fun List<String>.toHikeMap(): HikeMap {
    val positionsByElevation = List(10) { it }.associateWith { mutableSetOf<Position>() }
    forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            val elevation = c.digitToInt()
            positionsByElevation[elevation]?.add(Position(x, y))
        }
    }
    return HikeMap(positionsByElevation)
}
