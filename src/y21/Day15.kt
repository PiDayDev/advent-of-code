package y21

import kotlin.math.min

private const val day = "15"

fun main() {

    fun List<List<Int>>.shortestPath(): Int {
        val rIdx = indices
        val cIdx = first().indices
        val dist = MutableList(size) { MutableList(first().size) { 1e7.toInt() - 1 } }
        dist[0][0] = 0

        val unvisited = mutableListOf(0 to 0)
        val visited = mutableSetOf(0 to 0)

        fun Pair<Int, Int>.dijkstra(dr: Int, dc: Int) {
            val (r0, c0) = this
            val r = r0 + dr
            val c = c0 + dc
            if (r in rIdx && c in cIdx) {
                val old = dist[r][c]
                val next = dist[r0][c0] + this@shortestPath[r][c]
                dist[r][c] = min(old, next)
                unvisited.add(r to c)
            }
        }
        while (unvisited.isNotEmpty()) {
            val next = unvisited.minByOrNull { (r, c) -> dist[r][c] }!!
            unvisited.remove(next)
            visited.add(next)
            next.dijkstra(+1, 0)
            next.dijkstra(-1, 0)
            next.dijkstra(0, +1)
            next.dijkstra(0, -1)
            unvisited.removeAll(visited)
        }
        return dist.last().last()
    }

    fun List<String>.toRisks() =
        map { row -> row.split("").filter { it.isNotBlank() }.map { it.toInt() } }

    fun part1(input: List<String>) = input.toRisks().shortestPath()

    fun List<List<Int>>.inc() = map { row -> row.map { (it % 9) + 1 } }

    /* Very slow */
    fun part2(input: List<String>): Int {
        val risk0 = input.toRisks()
        val risks = (1..8).runningFold(risk0) { a, _ -> a.inc() }

        val columns = (0..4).map {
            risks[it + 0] + risks[it + 1] + risks[it + 2] + risks[it + 3] + risks[it + 4]
        }
        val largeMap =
            columns.reduce { a, b -> a.zip(b) { row1, row2 -> row1 + row2 } }

        return largeMap.shortestPath()
    }

    // test if implementation meets criteria from the description
    try {
        val testInput = readInput("Day${day}_test")
        val test1 = part1(testInput)
        check(test1 == 40) { "Test input result: $test1" }
        val test2 = part2(testInput)
        check(test2 == 315) { "Test input result: $test2" }
    } catch (e: java.io.FileNotFoundException) {
        // no tests
    }

    val input = readInput("Day$day")
    println(part1(input))
    println(part2(input))
}
