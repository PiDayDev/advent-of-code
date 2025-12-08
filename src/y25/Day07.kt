package y25

private const val DAY = "07"

fun main() {
    fun part1(input: List<String>): Int {
        val start = input.first().indexOf('S')
        var positions = setOf(start)
        var count = 0
        input.drop(1).forEach { row ->
            val newPositions = mutableSetOf<Int>()
            for (p in positions) {
                when (row[p]) {
                    '^' -> {
                        newPositions += setOf(p - 1, p + 1)
                        count++
                    }

                    else -> newPositions += p
                }
            }
            positions = newPositions
        }
        return count
    }

    fun part2(input: List<String>): Long {
        val start = input.first().indexOf('S')
        var positions = mapOf(start to 1L)
        input.drop(1).forEach { row ->
            val newPositions = mutableMapOf<Int, Long>()
            for ((p, count) in positions) {
                when (row[p]) {
                    '^' -> {
                        newPositions.merge(p - 1, count, Long::plus)
                        newPositions.merge(p + 1, count, Long::plus)
                    }

                    else -> {
                        newPositions.merge(p, count, Long::plus)
                    }
                }
            }
            positions = newPositions
        }
        return positions.values.sum()
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
