import Direction.*
import common.Point2D
import common.x
import common.y

private const val DAY = 23

enum class CardinalDirection(vararg val movements: Direction) {
    NW(UP, LEFT),
    N(UP),
    NE(UP, RIGHT),
    E(RIGHT),
    SE(DOWN, RIGHT),
    S(DOWN),
    SW(DOWN, LEFT),
    W(LEFT);

    fun neighbors(): List<CardinalDirection> {
        val v = values()
        val s = v.size
        return listOf(
            v[(ordinal + s - 1) % s],
            v[ordinal],
            v[(ordinal + 1) % s],
        )
    }
}

private operator fun Point2D.plus(d: Direction): Point2D = x + d.dx to y + d.dy

private operator fun Point2D.plus(c: CardinalDirection): Point2D = c.movements.fold(this) { a, d -> a + d }

private data class Grove(
    val elves: Set<Point2D>,
    val order: List<CardinalDirection> = listOf(CardinalDirection.N, CardinalDirection.S, CardinalDirection.W, CardinalDirection.E)
) {


    fun xRange() = elves.map { it.x }.sorted().let { it.first()..it.last() }
    fun yRange() = elves.map { it.y }.sorted().let { it.first()..it.last() }

    fun round(): Grove {
        val proposals = elves.associateWith { propose(it) }
        val unique = proposals.values.groupBy { it }.filterValues { it.size == 1 }.keys.toSet()
        val destinations = proposals.map { (elf, proposal) ->
            when (proposal) {
                in unique -> proposal
                else -> elf
            }
        }
        return Grove(destinations.toSet(), order.drop(1) + order.take(1))
    }

    private fun propose(elf: Point2D): Point2D {
        if (isAlone(elf)) return elf
        val proposal = order.firstNotNullOfOrNull { dir ->
            val lookAt = dir.neighbors().map { elf + it }
            if (lookAt.none { it in elves }) {
                elf + dir
            } else {
                null
            }
        }
        return proposal ?: elf
    }

    private fun isAlone(elf: Point2D) = CardinalDirection.values().none { elf + it in elves }

    override fun toString(): String {
        return "Grove(${elves.take(25)}...)"
    }
}

private fun List<String>.toGrove(): Grove {
    val elves = mutableSetOf<Point2D>()
    forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            when (c) {
                '#' -> elves += x to y
            }
        }
    }
    return Grove(elves)
}

fun main() {
    fun part1(grove: Grove): Int {
        val result = generateSequence(grove) { it.round() }
            .drop(10)
            .first()

        val size = result.xRange().toList().size * result.yRange().toList().size
        return size - result.elves.size
    }

    fun part2(grove: Grove): Int = 1 +
            generateSequence(grove) { it.round() }
                .windowed(2)
                .indexOfFirst { (a, b) -> a.elves == b.elves }

    val grove = readInput("Day${DAY}").toGrove()
    println(part1(grove))
    println(part2(grove))
}
