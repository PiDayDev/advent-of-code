package y23

import y23.Direction.*
import kotlin.math.max

private const val DAY = "16"

enum class Tile(val symbol: Char) {
    SPACE('.') {
        override fun process(beamDirection: Direction) = listOf(beamDirection)
    },
    MIRROR_SLASH('/') {
        override fun process(beamDirection: Direction) = when (beamDirection) {
            N -> E
            S -> W
            W -> S
            E -> N
        }.let { listOf(it) }
    },
    MIRROR_BACKSLASH('\\') {
        override fun process(beamDirection: Direction) = when (beamDirection) {
            N -> W
            S -> E
            W -> N
            E -> S
        }.let { listOf(it) }
    },
    SPLITTER_H('-') {
        override fun process(beamDirection: Direction) = when (beamDirection) {
            N, S -> listOf(W, E)
            else -> listOf(beamDirection)
        }
    },
    SPLITTER_V('|') {
        override fun process(beamDirection: Direction) = when (beamDirection) {
            W, E -> listOf(N, S)
            else -> listOf(beamDirection)
        }
    };

    abstract fun process(beamDirection: Direction): List<Direction>
}

fun Char.toTile() = Tile.values().first { it.symbol == this }

data class Beam(val position: Position, val direction: Direction)

fun main() {
    fun energize(contraption: Map<Position, Tile>, initialBeam: Beam): Int {
        val beams = mutableSetOf<Beam>()
        val queue = mutableSetOf(initialBeam)
        while (queue.isNotEmpty()) {
            val beam = queue.first()
            queue.remove(beam)

            val added = beams.add(beam)
            if (!added) continue

            val nextPosition = beam.position + beam.direction.movement
            if (nextPosition !in contraption) continue

            val nextTile = contraption[nextPosition]!!
            val nextDirections = nextTile.process(beam.direction)
            nextDirections.forEach { dir ->
                queue.add(Beam(nextPosition, dir))
            }
        }
        return (beams - initialBeam)
            .map { it.position }
            .distinct()
            .count()
    }

    fun List<String>.toContraption(): Map<Position, Tile> {
        val contraption: Map<Position, Tile> = flatMapIndexed { y, row ->
            row.mapIndexed { x, c ->
                val pos = Position(x, y)
                val tile = c.toTile()
                pos to tile
            }
        }
            .toMap()
        return contraption
    }

    fun part1(input: List<String>): Int {
        val contraption: Map<Position, Tile> = input.toContraption()
        val initialBeam = Beam(Position(x = -1, y = 0), E)
        return energize(contraption, initialBeam)
    }

    fun part2(input: List<String>): Int {
        val contraption: Map<Position, Tile> = input.toContraption()
        val yIndices = input.indices
        val xIndices = input.first().indices

        val maxVertical = yIndices.maxOf { y ->
            val leftBeam = Beam(Position(xIndices.first - 1, y), E)
            val rightBeam = Beam(Position(xIndices.last + 1, y), W)
            max(energize(contraption, leftBeam), energize(contraption, rightBeam))
        }
        val maxHorizontal = xIndices.maxOf { x ->
            val topBeam = Beam(Position(x, yIndices.first - 1), S)
            val bottomBeam = Beam(Position(x, yIndices.last + 1), N)
            max(energize(contraption, topBeam), energize(contraption, bottomBeam))
        }
        return max(maxHorizontal, maxVertical)
    }


    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
