package y22

import common.Point2D
import common.x
import common.y
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private const val DAY = 14

private data class Poly14(val points: List<Point2D>) {
    val xr: IntRange
        get() = points.map { it.x }.sorted().let { it.first()..it.last() }
    val yr: IntRange
        get() = points.map { it.y }.sorted().let { it.first()..it.last() }

    private val allPoints: Set<Point2D> = points.zipWithNext { a, b ->
        val x1 = min(a.x, b.x)
        val x2 = max(a.x, b.x)
        val y1 = min(a.y, b.y)
        val y2 = max(a.y, b.y)
        (x1..x2).flatMap { x ->
            (y1..y2).map { y ->
                x to y
            }
        }
    }
        .flatten()
        .toSet()

    operator fun contains(p: Point2D) = p in allPoints

    override fun toString(): String = toString(xr, yr, listOf(this))
}

private fun toString(xRange: IntRange, yRange: IntRange, rocks: List<Poly14>, sand: Collection<Point2D> = emptyList()): String {
    val b = StringBuilder()
    (1..3).forEach { line ->
        b.append("    ")
        xRange.forEach { x ->
            val k = (x / 10.0.pow(3 - line).toInt()) % 10
            if (line > 1 || k > 0) b.append(k) else b.append(" ")
        }
        b.append("\n")
    }
    yRange.forEach { y ->
        b.append(y.toString().padStart(4, ' '))
        xRange.forEach { x ->
            when {
                rocks.any { (x to y) in it } -> b.append("#")
                (x to y) in sand -> b.append("o")
                else -> b.append(".")
            }
        }
        b.append("\n")
    }

    return b.toString()
}

private fun String.toPoly() = split(" -> ")
    .map(String::toPoint)
    .let(::Poly14)

private fun String.toPoint() = split(",").let { (a, b) -> a.toInt() to b.toInt() }

private data class Cave14(val rocks: List<Poly14>, val sand: MutableSet<Point2D> = mutableSetOf()) {

    tailrec fun addSand(from: Point2D) {
        val (x, y) = from
        val destination = listOf(x to y + 1, x - 1 to y + 1, x + 1 to y + 1)
            .firstOrNull { isFree(it) }
        when {
            destination == null -> sand.add(from)
            y > yRockBottom -> return
            else -> addSand(destination)
        }
    }

    fun isFree(xy: Point2D) = xy !in sand && rocks.none { rock -> xy in rock }

    val yRockBottom by lazy { rocks.map { it.yr.last }.maxOf { it } }

    val yTop: Int
        get() = (rocks.map { it.yr.first } + sand.map { it.y }).minOf { it }
    val yBottom: Int
        get() = (rocks.map { it.yr.last } + sand.map { it.y }).maxOf { it }
    val xLeft: Int
        get() = (rocks.map { it.xr.first } + sand.map { it.x }).minOf { it }
    val xRight: Int
        get() = (rocks.map { it.xr.last } + sand.map { it.x }).maxOf { it }

    override fun toString(): String {
        return toString(xLeft..xRight, yTop..yBottom, rocks, sand)
    }
}

fun main() {
    val sandSource = 500 to 0

    fun part1(cave: Cave14): Int {
        var previous = -1
        var current = 0
        while (cave.sand.size > previous) {
            cave.addSand(sandSource)
            previous = current
            current = cave.sand.size
        }
        return current
    }

    fun part2(cave: Cave14): Int {
        val rocks = cave.rocks
        val yFloor = cave.yBottom + 2
        val maxDeltaX = yFloor + 2
        val floor = Poly14(listOf(500 - maxDeltaX to yFloor, 500 + maxDeltaX to yFloor))
        val finiteCave = Cave14(rocks + floor)
//        println(finiteCave)
        while (sandSource !in finiteCave.sand) {
            finiteCave.addSand(sandSource)
        }
//        println(finiteCave)
        return finiteCave.sand.size
    }

    val rocks = readInput("Day$DAY").map { it.toPoly() }
    println(part1(Cave14(rocks)))
    println(part2(Cave14(rocks)))
}
