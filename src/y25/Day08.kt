package y25

import java.lang.RuntimeException

private const val DAY = "08"

private fun Int.square() = this.toLong() * this.toLong()

private data class Box(val x: Int, val y: Int, val z: Int) {
    fun squaredDistance(other: Box) =
        ((x - other.x).square() + (y - other.y).square() + (z - other.z).square())
}

private fun String.toBox(): Box {
    val (x, y, z) = split(",").map(String::toInt)
    return Box(x, y, z)
}

private class DisjointSets(n: Int) {
    private val parent = IntArray(n) { it }

    fun find(x: Int): Int {
        if (parent[x] != x) parent[x] = find(parent[x]) // Path Compression
        return parent[x]
    }

    fun union(x: Int, y: Int) {
        val rootX = find(x)
        val rootY = find(y)
        if (rootX != rootY) parent[rootX] = rootY
    }

    fun sizes (): List<Int> {
        val counts = parent.indices.groupingBy { find(it) }.eachCount()
        return counts.values.sortedDescending()
    }
}

fun main() {
    fun computeDistances(boxes: List<Box>): Map<Pair<Int, Int>, Long> {
        val distances = mutableMapOf<Pair<Int, Int>, Long>()
        boxes.forEachIndexed { i, box ->
            (i + 1..boxes.lastIndex).forEach { j ->
                distances[i to j] = box.squaredDistance(boxes[j])
            }
        }
        return distances
    }

    fun part1(
        boxes: List<Box>,
        distances: Map<Pair<Int, Int>, Long>,
        usedConnections: Int
    ): Int {
        val connections = distances
            .toList()
            .sortedBy { (_, d) -> d }
            .take(usedConnections)
            .map { it.first }

        val sets = DisjointSets(boxes.size)
        for ((a, b) in connections) sets.union(a, b)

        val largestThree = sets.sizes().take(3)
        return largestThree.reduce(Int::times)
    }

    fun part2(boxes: List<Box>, distances: Map<Pair<Int, Int>, Long>): Int {
        val connections = distances
            .toList()
            .sortedBy { (_, d) -> d }
            .map { it.first }

        val sets = DisjointSets(boxes.size)
        for ((a, b) in connections) {
            sets.union(a, b)
            if (sets.sizes().size == 1)
                return boxes[a].x * boxes[b].x
        }
        throw RuntimeException("Solution not found - there must be a bug")
    }

    val input = readInput("Day$DAY").map(String::toBox)
    val distances = computeDistances(input)
    println(part1(input, distances, 1000))
    println(part2(input, distances))
}
