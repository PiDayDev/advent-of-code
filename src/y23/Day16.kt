package y23

import kotlin.math.max

private const val DAY = "16"

enum class Dir(val movement: Position) { N(north), S(south), W(west), E(east) }

enum class Tile(val symbol: Char) {
    SPACE('.') {
        override fun process(beamDirection: Dir) = listOf(beamDirection)
    },
    MIRROR_SLASH('/') {
        override fun process(beamDirection: Dir) = when (beamDirection) {
            Dir.N -> Dir.E
            Dir.S -> Dir.W
            Dir.W -> Dir.S
            Dir.E -> Dir.N
        }.let { listOf(it) }
    },
    MIRROR_BACKSLASH('\\') {
        override fun process(beamDirection: Dir) = when (beamDirection) {
            Dir.N -> Dir.W
            Dir.S -> Dir.E
            Dir.W -> Dir.N
            Dir.E -> Dir.S
        }.let { listOf(it) }
    },
    SPLITTER_H('-') {
        override fun process(beamDirection: Dir) = when (beamDirection) {
            Dir.N, Dir.S -> listOf(Dir.W, Dir.E)
            else -> listOf(beamDirection)
        }
    },
    SPLITTER_V('|') {
        override fun process(beamDirection: Dir) = when (beamDirection) {
            Dir.W, Dir.E -> listOf(Dir.N, Dir.S)
            else -> listOf(beamDirection)
        }
    };

    abstract fun process(beamDirection: Dir): List<Dir>
}

fun Char.toTile() = Tile.values().first { it.symbol == this }

data class Beam(val position: Position, val direction: Dir)

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
        val initialBeam = Beam(Position(x = -1, y = 0), Dir.E)
        return energize(contraption, initialBeam)
    }

    fun part2(input: List<String>): Int {
        val contraption: Map<Position, Tile> = input.toContraption()
        val yIndices = input.indices
        val xIndices = input.first().indices

        val maxVertical = yIndices.maxOf { y ->
            val leftBeam = Beam(Position(xIndices.first - 1, y), Dir.E)
            val rightBeam = Beam(Position(xIndices.last + 1, y), Dir.W)
            max(energize(contraption, leftBeam), energize(contraption, rightBeam))
        }
        val maxHorizontal = xIndices.maxOf { x ->
            val topBeam = Beam(Position(x, yIndices.first - 1), Dir.S)
            val bottomBeam = Beam(Position(x, yIndices.last + 1), Dir.N)
            max(energize(contraption, topBeam), energize(contraption, bottomBeam))
        }
        return max(maxHorizontal, maxVertical)
    }


    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
