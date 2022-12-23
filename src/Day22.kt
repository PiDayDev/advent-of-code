import common.Point2D
import common.x
import common.y

private const val DAY = 22

private data class ForceFieldBoard(
    val tiles: Collection<Point2D>,
    val walls: Collection<Point2D>,
    val mode: Mode = Mode.FLAT
) {
    enum class Mode {
        FLAT, CUBE
    }

    var direction: Direction = Direction.RIGHT
    var position: Point2D = tiles.topRow().minByOrNull { it.x }!!

    private val wholeMap = tiles + walls
    private val topLimitByCol = wholeMap.groupBy { it.x }.mapValues { (_, points) -> points.minByOrNull { it.y }!! }
    private val bottomLimitByCol = wholeMap.groupBy { it.x }.mapValues { (_, points) -> points.maxByOrNull { it.y }!! }
    private val leftLimitByRow = wholeMap.groupBy { it.y }.mapValues { (_, points) -> points.minByOrNull { it.x }!! }
    private val rightLimitByRow = wholeMap.groupBy { it.y }.mapValues { (_, points) -> points.maxByOrNull { it.x }!! }

    fun exec(command: String) {
        val moves = command.toIntOrNull()
        when {
            command == "L" -> direction = direction.rotateLeft()
            command == "R" -> direction = direction.rotateRight()
            moves != null -> repeat(moves) { advance() }
            else -> command.split("""(?<=[LR])|(?=[LR])""".toRegex())
                .filter { it.isNotBlank() }
                .forEach { exec(it) }
        }
    }

    private fun advance() {
        when (val candidate: Point2D = position.x + direction.dx to position.y + direction.dy) {
            in tiles -> position = candidate
            !in walls -> {
                val (wrappedPos, wrappedDir) = when (mode) {
                    Mode.FLAT -> wrapAroundFlat()
                    Mode.CUBE -> wrapAroundCube()
                }
                if (wrappedPos in tiles) {
                    position = wrappedPos
                    direction = wrappedDir
                }
            }
        }
    }

    private fun wrapAroundFlat(): Pair<Point2D, Direction> = when (direction) {
        Direction.UP -> bottomLimitByCol[position.x]
        Direction.DOWN -> topLimitByCol[position.x]
        Direction.LEFT -> rightLimitByRow[position.y]
        Direction.RIGHT -> leftLimitByRow[position.y]
    }!! to direction

    private fun wrapAroundCube(): Pair<Point2D, Direction> {
        /*                AB
         * Cube model:    C
         *               DE
         *               F
         */
        val faceSize = 50
        val p1 = 1 * faceSize
        val p2 = 2 * faceSize
        val p3 = 3 * faceSize
        val p4 = 4 * faceSize

        val (x, y) = position

        val isAB = y < p1
        val isDE = y in p2 until p3
        val isACE = x in p1 until p2

        val isA = isAB && isACE
        val isB = isAB && !isACE
        val isC = y in p1 until p2
        val isD = isDE && !isACE
        val isE = isDE && isACE
        val isF = y in p3 until p4

        /* Connections:
         *  - A left  / D left   (inverted)
         *  - B right / E right  (inverted)
         *  - B up    / F down   (straight)
         *  - B down  / C right  (straight)
         *  - E down  / F right  (straight)
         *  - A up    / F left   (straight)
         *
         *  - C left  / D up     (inverted)
         */
        val xn = x % faceSize
        val yn = y % faceSize
        return when {
            isA && direction == Direction.LEFT -> (0 to p3 - yn - 1) to Direction.RIGHT
            isD && direction == Direction.LEFT -> (p1 to p1 - yn - 1) to Direction.RIGHT
            isB && direction == Direction.RIGHT -> (p2 - 1 to p3 - yn - 1) to Direction.LEFT
            isE && direction == Direction.RIGHT -> (p3 - 1 to p1 - yn - 1) to Direction.LEFT
            isB && direction == Direction.UP -> (xn to p4 - 1) to Direction.UP
            isF && direction == Direction.DOWN -> (p2 + xn to 0) to Direction.DOWN
            isB && direction == Direction.DOWN -> (p2 - 1 to p1 + xn) to Direction.LEFT
            isC && direction == Direction.RIGHT -> (p2 + yn to p1 - 1) to Direction.UP
            isE && direction == Direction.DOWN -> (p1 - 1 to p3 + xn) to Direction.LEFT
            isF && direction == Direction.RIGHT -> (p1 + yn to p3 - 1) to Direction.UP
            isA && direction == Direction.UP -> (0 to p3 + xn) to Direction.RIGHT
            isF && direction == Direction.LEFT -> (p1 + yn to 0) to Direction.DOWN
            isC && direction == Direction.LEFT -> (p1 - yn - 1 to p2) to Direction.DOWN
            isD && direction == Direction.UP -> (p1 to p2 - xn - 1) to Direction.RIGHT
            else -> position to direction
        }
    }

    fun password(): Int {
        val row = 1 + position.y
        val col = 1 + position.x
        val facing = getFacing(direction)
        return row * 1000 + col * 4 + facing
    }

    private fun getFacing(direction: Direction) = when (direction) {
        Direction.RIGHT -> 0
        Direction.DOWN -> 1
        Direction.LEFT -> 2
        Direction.UP -> 3
    }

    private fun Direction.rotateLeft(): Direction = when (this) {
        Direction.UP -> Direction.LEFT
        Direction.DOWN -> Direction.RIGHT
        Direction.LEFT -> Direction.DOWN
        Direction.RIGHT -> Direction.UP
    }

    private fun Direction.rotateRight(): Direction = rotateLeft().rotateLeft().rotateLeft()

    private fun Collection<Point2D>.topRow(): Collection<Point2D> {
        val minY = minOf { it.y }
        return filter { it.y == minY }
    }
}

private fun List<String>.toForceFieldBoard(): ForceFieldBoard {
    val tiles = mutableSetOf<Point2D>()
    val walls = mutableSetOf<Point2D>()
    forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            when (c) {
                '.' -> tiles += x to y
                '#' -> walls += x to y
            }
        }
    }
    return ForceFieldBoard(tiles, walls)
}

fun main() {

    fun part1(board: ForceFieldBoard, command: String): Int {
        board.exec(command)
        return board.password()
    }

    fun part2(board: ForceFieldBoard, command: String) =
        part1(board.copy(mode = ForceFieldBoard.Mode.CUBE), command)

    val input = readInput("Day${DAY}")
    val board = input.dropLast(1).toForceFieldBoard()
    val command = input.last()

    println(part1(board, command))
    println(part2(board, command))
}
