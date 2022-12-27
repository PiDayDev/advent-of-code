import Direction.*
import common.Point2D
import common.x
import common.y

private const val DAY = 24

private data class Valley(
    val walls: Set<Point2D>,
    val blizzards: Map<Direction, Collection<Point2D>>,
    val width: Int,
    val height: Int
) {
    val entrance = (0 until width).map { x -> x to 0 }.first { it !in walls }
    val exit = (0 until width).map { x -> x to height - 1 }.first { it !in walls }

    private fun Point2D.warp(direction: Direction): Point2D =
        when (direction) {
            UP -> x to height - 2
            DOWN -> x to 1
            LEFT -> width - 2 to y
            RIGHT -> 1 to y
        }

    fun evolve(): Valley = copy(blizzards = blizzards
        .mapValues { (direction, points) ->
            points.map {
                val moved = it + direction
                if (moved in walls) {
                    moved.warp(direction)
                } else {
                    moved
                }
            }
        })

    override fun toString() = "Valley(entrance=$entrance, exit=$exit)"

    fun filterValid(candidates: List<Point2D>) = candidates
        .asSequence()
        .filter { it.x in 0 until width }
        .filter { it.y in 0 until height }
        .filter { it !in walls }
        .filter { it !in blizzards.values.flatten() }
        .toList()

}

private data class Expedition(val valley: Valley, val positions: Collection<Point2D>, val time: Int)

fun main() {
    fun List<String>.toValley(): Valley {
        val walls: MutableSet<Point2D> = mutableSetOf()
        val blizzards: Map<Direction, MutableSet<Point2D>> =
            Direction.values().associateWith { mutableSetOf() }
        forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                val point = x to y
                when (c) {
                    '#' -> walls += point
                    '^' -> blizzards[UP]!! += point
                    'v' -> blizzards[DOWN]!! += point
                    '<' -> blizzards[LEFT]!! += point
                    '>' -> blizzards[RIGHT]!! += point
                }
            }
        }
        return Valley(walls, blizzards, width = first().length, height = size)
    }

    fun navigate(valley: Valley, start: Point2D, goal: Point2D): Expedition =
        generateSequence(Expedition(valley, listOf(start), 0)) { (v, positions, t) ->
            val nextValley = v.evolve()
            val candidates = positions.flatMap { p -> values().map { d -> p + d } + p }.distinct()
            val nextPositions = nextValley.filterValid(candidates)
            Expedition(nextValley, nextPositions, t + 1)
        }
            .first { goal in it.positions }

    fun part1(valley: Valley) =
        navigate(valley, valley.entrance, valley.exit).time

    fun part2(valley: Valley): Int {
        val entrance = valley.entrance
        val exit = valley.exit
        val leg1 = navigate(valley, entrance, exit)
        val leg2 = navigate(leg1.valley, exit, entrance)
        val leg3 = navigate(leg2.valley, entrance, exit)
        return leg1.time + leg2.time + leg3.time
    }

    val valley = readInput("Day${DAY}").toValley()
    println(part1(valley))
    println(part2(valley))
}
