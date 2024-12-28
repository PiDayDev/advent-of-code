package y18

private const val input = 5535

fun main() {
    val memo2 = mutableMapOf<Pair<Int, Int>, Int>()
    val memo3 = mutableMapOf<Triple<Int, Int, Int>, Int>()

    fun power(x: Int, y: Int): Int = memo2.getOrPut(x to y) {
        val rack = 10 + x
        val power = (rack * y + input) * rack
        (power / 100) % 10 - 5
    }

    fun power(x: Int, y: Int, size: Int): Int = memo3.getOrPut(Triple(x, y, size)) {
        when {
            size == 1 -> power(x, y)
            size % 2 == 0 -> {
                val z = size / 2
                // sum four squares
                power(x, y, z) + power(x + z, y, z) +
                        power(x, y + z, z) + power(x + z, y + z, z)
            }

            else -> {
                val z = size - 1
                // sum smaller square + bottom side + right side - bottom right corner
                power(x, y, z) - power(x + z, y + z) + (0..z).sumOf { d ->
                    power(x + d, y + z) + power(x + z, y + d)
                }
            }
        }
    }

    fun xySequence(): Sequence<Pair<Int, Int>> {
        val range = (1..300).asSequence()
        return range
            .flatMap { x -> range.map { y -> x to y } }
            .sortedBy { (x, y) -> x + y }
    }

    fun part1(): Pair<Int, Int> =
        xySequence().maxBy { (x, y) -> power(x, y, 3) }

    fun part2(maxSize: Int): Triple<Int, Int, Int> {
        val p2 = (1..maxSize)
            .flatMap { size ->
                xySequence()
                    .filter { (x, y) -> x + size <= 300 && y + size <= 300 }
                    .map { (x, y) -> Triple(x, y, size) }
            }
            .maxBy { (x, y, size) -> power(x, y, size) }
        return p2
    }

    println(part1())

    // Should actually use maxSize = 300, making this much slower
    println(part2(maxSize = 15))
}
