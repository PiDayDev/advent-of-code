package y24

import kotlin.math.hypot
import kotlin.math.pow

private const val DAY = "14"

typealias Velocity = Position

data class Quadrant(val xRange: IntRange, val yRange: IntRange) {

    operator fun contains(position: Position) =
        position.x in xRange && position.y in yRange

}

infix fun List<Position>.countIn(quadrant: Quadrant): Int = count { it in quadrant }

data class Grid(val width: Int, val height: Int) {

    fun constrain(position: Position): Position {
        val x = (position.x % width + width) % width
        val y = (position.y % height + height) % height
        return Position(x, y)
    }

    fun quadrants(): List<Quadrant> {
        val xMiddle = width / 2
        val yMiddle = height / 2
        val (x1, x2, x3, x4) = listOf(0, xMiddle - 1, xMiddle + 1, width - 1)
        val (y1, y2, y3, y4) = listOf(0, yMiddle - 1, yMiddle + 1, height - 1)
        return listOf(
            Quadrant(x1..x2, y1..y2),
            Quadrant(x3..x4, y1..y2),
            Quadrant(x1..x2, y3..y4),
            Quadrant(x3..x4, y3..y4),
        )
    }

    fun safetyFactor(positions: List<Position>): Int = quadrants()
        .map { positions countIn it }
        .reduce { a, b -> a * b }

}


data class Robot(val position: Position, val velocity: Velocity) {

    fun move(): Robot = copy(position = position + velocity)
    fun moveInside(grid: Grid): Robot {
        val newRobot = move()
        return copy(position = grid.constrain(newRobot.position))
    }
}

fun String.toRobot(): Robot {
    val p = substringAfter("p=").substringBefore(" ")
    val v = substringAfter("v=")

    fun String.toInts() = split(",").map { it.toInt() }
    val (px, py) = p.toInts()
    val (vx, vy) = v.toInts()

    return Robot(Position(px, py), Velocity(vx, vy))
}


fun safetyFactor(input: List<String>, grid: Grid): Int {
    val finalPositions = (1..100)
        .fold(input.map { it.toRobot() }) { robots, _ ->
            robots.map { it.moveInside(grid) }
        }
        .map { it.position }
    return grid.safetyFactor(finalPositions)
}

fun main() {
    val mainGrid = Grid(101, 103)

    fun part1(input: List<String>) = safetyFactor(input, mainGrid)

    fun printRobots(robots: List<Robot>, grid: Grid) {
        val positions = robots.map { it.position }.toSet()
        (0 until grid.height).forEach { y ->
            val string = (0 until grid.width).joinToString("") { x ->
                when (Position(x, y)) {
                    in positions -> "█"
                    else -> "."
                }
            }
            println(string)
        }
    }

    fun part2(input: List<String>): Int {
        val initialRobots = input.map { it.toRobot() }
        val maxTries = mainGrid.width * mainGrid.height

        fun Set<Position>.variance(): Double {
            val avgX = map { it.x }.average()
            val avgY = map { it.y }.average()
            return map { hypot(avgX - it.x, avgY - it.y).pow(2) }.average()
        }

        val (bestTime, bestRobots) = generateSequence(0 to initialRobots) { (time, robots) ->
            time + 1 to robots.map { it.moveInside(mainGrid) }
        }
            .take(maxTries)
            .minBy { (_, bots) -> bots.map { it.position }.toSet().variance() }

        printRobots(bestRobots, mainGrid)

        return bestTime
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
