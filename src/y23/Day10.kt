package y23

private const val DAY = "10"

data class Position(val x: Int, val y: Int) {
    operator fun plus(p: Position) = Position(x + p.x, y + p.y)
    fun around() = listOf(north, south, east, west).map { dir -> this + dir }
    override fun toString(): String = "($x,$y)"
}

val north = Position(x = 0, y = -1)
val south = Position(x = 0, y = +1)
val east = Position(x = 1, y = 0)
val west = Position(x = -1, y = 0)

private enum class Shape(val char: Char, vararg val neighbors: Position) {
    NS('|', north, south),
    EW('-', east, west),
    NE('L', north, east),
    NW('J', north, west),
    SE('F', south, east),
    SW('7', south, west),
    G('.'),
    S('S', north, south, west, east);
}

private fun Char.toShape() = Shape.values().firstOrNull { it.char == this } ?: Shape.G

private data class Pipe(val shape: Shape, val position: Position) {
    fun neighbors() = shape.neighbors.map { position + it }
}

private fun computeDistances(input: List<String>): Map<Position, Int> {
    val pipes: List<Pipe> = input.flatMapIndexed { y, row ->
        row.mapIndexed { x, c ->
            Pipe(c.toShape(), Position(x, y))
        }
    }
    val pipesMap: Map<Position, Pipe> = pipes.associateBy { it.position }
    val start: Pipe = pipes.first { it.shape == Shape.S }
    val startPosition = start.position
    val startNeighbors = start.neighbors()
        .filter { pos: Position -> startPosition in pipesMap[pos]!!.neighbors() }

    val distances: MutableMap<Position, Int> = mutableMapOf(startPosition to 0)
    distances += startNeighbors.associateWith { 1 }

    val queue = startNeighbors.toMutableSet()

    while (queue.isNotEmpty()) {
        val nextDistance = distances[queue.first()]!! + 1
        val next = queue.flatMap { pipesMap[it]?.neighbors() ?: emptyList() } - distances.keys
        queue.clear()
        queue += next
        next.forEach { distances[it] = nextDistance }
    }

    return distances
}

fun main() {
    val input = readInput("Day$DAY")
    val distances: Map<Position, Int> = computeDistances(input)

    fun part1() = distances.maxOf { it.value }

    fun part2(): Int {
        val map = mutableListOf<String>()

        // zoom-out the pipe maze: each cell of the map becomes a 3x3 square, using:
        // - "█" for pipes in the main loop and the entire 3x3 starting square 
        // - "█" (again) for the center cell of every other 3x3 square
        // - " " for everything else
        input.forEachIndexed { y, row ->
            val threeRows = MutableList(3) { "" }
            row.forEachIndexed { x, c ->
                val output =
                    if (Position(x, y) in distances.keys) {
                        block(c)
                    } else {
                        block('.')
                    }
                output.forEachIndexed { index, s -> threeRows[index] += s }
            }
            map += threeRows
        }
        map.forEach { println(it) }

        // start from north-west, go south-east until entering
        var origin = Position(x = 1, y = 0)
        val se = south + east
        while (map[origin.y][origin.x] == ' ') {
            origin += se
        }
        // one more step: we are inside the loop
        origin += se

        return floodFillAndCountSurrounded(map, origin)
    }

    println(part1())
    println(part2())
}


private fun block(c: Char ): List<String> = when (c) {
    '|' -> listOf(
        " █ ",
        " █ ",
        " █ ",
    )

    '-' -> listOf(
        "   ",
        "███",
        "   ",
    )

    'L' -> listOf(
        " █ ",
        " ██",
        "   ",
    )

    'J' -> listOf(
        " █ ",
        "██ ",
        "   ",
    )

    'F' -> listOf(
        "   ",
        " ██",
        " █ ",
    )

    '7' -> listOf(
        "   ",
        "██ ",
        " █ ",
    )

    'S' -> listOf(
        "███",
        "███",
        "███",
    )

    else -> listOf(
        "   ",
        " █ ",
        "   ",
    )
}

private fun floodFillAndCountSurrounded(map: MutableList<String>, origin: Position): Int {
    val filled = mutableSetOf(origin)
    val queue = mutableSetOf(origin)
    while (queue.isNotEmpty()) {
        val neighbors = queue.flatMap { it.around() }
        queue.clear()

        queue += neighbors.filter { map[it.y][it.x] == ' ' } - filled
        filled += queue
    }
    var surroundedCount = 0
    map.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            val pos = Position(x, y)
            print(if (pos in filled) 'I' else c)
            if (map[pos.y][pos.x] != ' ' && pos.around().all { it in filled })
                surroundedCount++
        }
        println()
    }
    return surroundedCount
}

