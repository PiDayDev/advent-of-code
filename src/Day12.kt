import common.Point2D
import common.x
import common.y

private const val DAY = 12

private operator fun List<String>.get(xy: Point2D): Char =
    this[xy.y][xy.x]

private fun List<String>.altitude(xy: Point2D) =
    when (val cell = this[xy]) {
        'S' -> 'a'
        'E' -> 'z'
        else -> cell
    }.code

private fun List<String>.destinations(xy: Point2D): List<Point2D> {
    val z = altitude(xy)
    val (x, y) = xy
    return listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1)
        .filter { (x, y) -> x in first().indices && y in indices }
        .filter { altitude(it) <= z + 1 }
}


fun main() {

    fun solveFor(input: List<String>, x0: Int, y0: Int, maxLimit: Int = 99999): Int? {
        val yF = input.indexOfFirst { "E" in it }
        val xF = input[yF].indexOf("E")
        val h = input.size
        val w = input.first().length

        val costs = mutableMapOf((x0 to y0) to 0)

        var currentCost = 0
        var oldSize = 0
        while (costs.size in oldSize + 1 until w * h && currentCost <= maxLimit) {
            oldSize = costs.size
            val edge = costs.keys.filter { costs[it] == currentCost }
            currentCost++
            edge.flatMap { input.destinations(it) }
                .forEach { costs.putIfAbsent(it, currentCost) }
        }

        return costs[xF to yF]
    }

    fun part1(input: List<String>): Int {
        val y0 = input.indexOfFirst { "S" in it }
        val x0 = input[y0].indexOf("S")
        return solveFor(input, x0, y0)!!
    }

    fun part2(input: List<String>) =
        input.mapIndexedNotNull { y, row ->
            row.mapIndexedNotNull { x, c ->
                if (c == 'a') {
                    solveFor(input, x, y)
                } else
                    null
            }.minOrNull()
        }.minOrNull()!!

    val input = readInput("Day${DAY}")
    println(part1(input))
    println(part2(input))

}
