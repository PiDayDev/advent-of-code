package y24

private const val DAY = "15"

fun main() {
    val input = readInput("Day$DAY")

    val warehouse = input
        .takeWhile { it.isNotBlank() }
        .toWarehouse()

    val botMoves = input
        .takeLastWhile { it.isNotBlank() }
        .toBotMoves()

    val p1 = warehouse.applyMovements(botMoves).gpsTotal()
    val p2 = warehouse.widen().applyMovements(botMoves).gpsTotal()

    println(p1)
    println(p2)

    check(p1 == 1497888) { "My actual solution for part 1" }
    check(p2 == 1522420) { "My actual solution for part 2" }
}

private enum class BoxWidth(val size: Int) {
    STANDARD(1),
    WIDE(2)
}

private data class Warehouse(
    val bot: Position,
    val walls: Set<Position>,
    val boxes: Set<Position>,
    val boxWidth: BoxWidth
) {
    private val explodedBoxes = boxes.withActualSize()

    private fun Collection<Position>.withActualSize() = when (boxWidth) {
        BoxWidth.WIDE -> toSet() + map { it + Direction.E }
        BoxWidth.STANDARD -> toSet()
    }

    fun gpsTotal() = boxes.sumOf { it.gps() }

    fun applyMovements(botMoves: BotMoves) = botMoves.movements
        .fold(this) { warehouse, direction ->
            warehouse.moveBot(direction)
//                .also { println(direction); it.dump() }
        }

    private fun moveBot(dir: Direction): Warehouse = when (dir) {
        Direction.N, Direction.S -> moveBotVertically(dir)
        Direction.W, Direction.E -> moveBotHorizontally(dir)
    }

    private fun moveBotHorizontally(dir: Direction): Warehouse {
        val nextBotPosition = bot + dir
        val (x, y) = nextBotPosition
        val delta = dir.movement.x * boxWidth.size

        fun collidesWithWalls(pos: Position) = pos in walls || pos - dir.movement * (boxWidth.size - 1) in walls

        val involved = generateSequence(x) { it + delta }
            .map { Position(it, y) }
            .takeWhile { pos -> pos in explodedBoxes || collidesWithWalls(pos) }
            .toList()

        if (involved.any(::collidesWithWalls)) return this

        return moveBotAndActualInvolvedBoxes(involved, dir)
    }

    private fun moveBotVertically(dir: Direction): Warehouse {
        val nextBotPosition = bot + dir
        val (x, y) = nextBotPosition
        var edge = setOf(Position(x, y))
        val involved = mutableSetOf<Position>()
        while (true) {
            if (walls.intersect(edge).isNotEmpty()) return this
            val touched = explodedBoxes.intersect(edge)
            if (touched.isEmpty()) break
            val (actualBoxes, otherSides) = touched.partition { it in boxes }
            val allMovedBoxes = actualBoxes + otherSides.map { it + Direction.W }
            involved += allMovedBoxes
            edge = allMovedBoxes.map { it + dir }.withActualSize()
        }
        return moveBotAndActualInvolvedBoxes(involved, dir)
    }

    private fun moveBotAndActualInvolvedBoxes(involved: Collection<Position>, dir: Direction): Warehouse {
        val (actualBoxes, otherSides) = involved.toSet().partition { it in boxes }
        val allMovedBoxes = actualBoxes.toSet() + otherSides.map { it + Direction.W }
        check(boxWidth == BoxWidth.WIDE || actualBoxes.size == allMovedBoxes.size) { "Should have no effect on standard warehouse" }
        val newBoxes = boxes - allMovedBoxes + allMovedBoxes.map { it + dir }
        return copy(bot = bot + dir, boxes = newBoxes)
    }

    fun widen(): Warehouse {
        val newBot = bot.copy(x = 2 * bot.x)
        val newWalls = walls.flatMap { w -> listOf(w.copy(x = 2 * w.x), w.copy(x = 2 * w.x + 1)) }
        val newBoxes = boxes.map { b -> b.copy(x = 2 * b.x) }
        return Warehouse(newBot, newWalls.toSet(), newBoxes.toSet(), BoxWidth.WIDE)
    }

    fun dump() {
        val elements = walls + boxes + bot
        val xRange = 0..elements.maxOf { it.x }
        val yRange = 0..elements.maxOf { it.y }

        print(" ".repeat(5))
        xRange.forEach { x -> print(x % 10) }
        println()
        yRange.forEach { y ->
            print("$y".padStart(4) + " ")
            xRange.forEach { x ->
                val c = when (Position(x, y)) {
                    bot -> "@"
                    in walls -> "#"
                    in boxes -> "["
                    in explodedBoxes -> "]"
                    else -> "."
                }
                print(c)
            }
            println()
        }
    }

}

private fun List<String>.toWarehouse(): Warehouse {
    val raw = classifyByChar()
    val bot = raw['@']!!.first()
    val walls = raw['#']!!.toSet()
    val boxes = raw['O']!!.toSet()
    return Warehouse(bot, walls, boxes, BoxWidth.STANDARD)
}

private data class BotMoves(val movements: List<Direction>)

private fun List<String>.toBotMoves() = joinToString("")
    .mapNotNull {
        when (it) {
            '^' -> Direction.N
            'v' -> Direction.S
            '<' -> Direction.W
            '>' -> Direction.E
            else -> null
        }
    }.let {
        BotMoves(it)
    }

private fun Position.gps() = 100 * y + x