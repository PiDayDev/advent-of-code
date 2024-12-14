package y24

private const val DAY = "14"

typealias Velocity = Position

data class Quadrant(
    val xRange: IntRange,
    val yRange: IntRange,
)

data class Grid(val width: Int, val height: Int) {
    fun constrain(newRobot: Robot): Position {
        val x = newRobot.position.x
        val y = newRobot.position.y
        val newX = (x % width + width) % width
        val newY = (y % height + height) % height
        return Position(newX, newY)
    }

    fun quadrants(): List<Quadrant> {
        val xMiddle = width/2
        val yMiddle = height/2
        val (x1,x2,x3,x4) = listOf(0,xMiddle-1,xMiddle+1,width-1)
        val (y1,y2,y3,y4) = listOf(0,yMiddle-1,yMiddle+1,height-1)
        return listOf(
            Quadrant(x1..x2,y1..y2),
            Quadrant(x3..x4,y1..y2),
            Quadrant(x1..x2,y3..y4),
            Quadrant(x3..x4,y3..y4),
        )
    }
}

data class Robot(val position: Position, val velocity: Velocity) {

    fun move(): Robot {
        val newPosition = position + velocity
        return copy(position = newPosition)
    }

    fun moveInside(grid: Grid): Robot {
        val newRobot = this.move()
        return copy(position = grid.constrain(newRobot))
    }
}

fun String.toRobot(): Robot {
    val p = substringAfter("p=").substringBefore(" ")
    val v = substringAfter("v=")
    val (px, py) = p.split(",").map { it.toInt() }
    val (vx, vy) = v.split(",").map { it.toInt() }
    return Robot(
        Position(px, py),
        Velocity(vx, vy)
    )
}

fun parseAndMove100Times(input: List<String>, grid: Grid): List<Position> =
    (1..100)
        .fold(input.map { it.toRobot() }) { robots, _ ->
            robots.map { it.moveInside(grid) }
        }
        .map { it.position }


fun List<Position>.countIn(xRange: IntRange, yRange: IntRange): Int =
    count { position -> position.x in xRange && position.y in yRange }

fun safetyFactor(input: List<String>, grid: Grid): Int {
 val finalPositions = parseAndMove100Times(input, grid)
    val quadrants = grid.quadrants()

    val counts = quadrants.map { finalPositions.countIn(it.xRange, it.yRange) }

    return counts.reduce { a,b -> a * b }
}

fun main() {
    fun part1(input: List<String>): Int {
        val grid = Grid(101,103)
        return safetyFactor(input, grid)
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
