package y22

private const val DAY = 18

private data class Cube(val x: Int, val y: Int, val z: Int) {
    fun neighbors() = setOf(
        copy(x = x + 1), copy(x = x - 1),
        copy(y = y + 1), copy(y = y - 1),
        copy(z = z + 1), copy(z = z - 1),
    )

    fun expandAround(obstacles: Collection<Cube>): Set<Cube> =
        neighbors() - obstacles.toSet()

    override fun toString() = "($x,$y,$z)"
}

private fun Collection<Cube>.floodAround(obstacles: Collection<Cube>, coordinateRange: IntRange): Set<Cube> {
    var current = this
    val accumulator = this.toMutableSet()
    while (current.isNotEmpty()) {
        current = current
            .flatMap { it.expandAround(obstacles) }
            .asSequence()
            .minus(accumulator)
            .filter { it.x in coordinateRange }
            .filter { it.y in coordinateRange }
            .filter { it.z in coordinateRange }
            .toSet()
        accumulator += current
    }
    return accumulator
}

fun main() {
    fun String.toCube() = split(",")
        .map { it.toInt() }
        .let { (a, b, c) -> Cube(a, b, c) }

    fun part1(cubes: List<Cube>) = cubes
        .sumOf { it.neighbors().count { n -> n !in cubes } }

    fun part2(cubes: List<Cube>): Int {
        val range = cubes.flatMap { listOf(it.x, it.y, it.z) }.sorted().let { it.first()..it.last() }
        val domain = range.flatMap { x -> range.flatMap { y -> range.map { z -> Cube(x, y, z) } } }
        val start = listOf(Cube(range.first, range.first, range.first))
        val air = start.floodAround(obstacles = cubes, coordinateRange = range)
        return part1(domain - air)
    }

    val input = readInput("Day$DAY")
    val cubes = input.map { it.toCube() }
    println(part1(cubes))
    println(part2(cubes))
}
