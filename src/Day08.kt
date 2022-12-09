private const val DAY = "08"

fun main() {
    fun part1(input: List<String>): Int {
        var count = 0
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, elem ->
                fun List<Char>.doesNotCover() = none { it >= elem }

                val left = row.take(x).toList()
                val right = row.drop(x + 1).toList()
                val up = input.take(y).map { it[x] }
                val down = input.drop(y + 1).map { it[x] }

                if (listOf(left, right, up, down).any { it.doesNotCover() })
                    count++
            }
        }
        return count
    }

    fun List<String>.visibleTrees(y: Int, x: Int, d: Direction): Int {
        val tree = this[y][x]
        var xCurr = x
        var yCurr = y
        var count = 0
        while (true) {
            xCurr += d.dx
            yCurr += d.dy
            if (yCurr !in indices || xCurr !in first().indices)
                break

            count++
            if (this[yCurr][xCurr] >= tree)
                break
        }
        return count
    }

    fun List<String>.scenicScore(y: Int, x: Int): Int {
        val up = visibleTrees(y, x, Direction.UP)
        val dn = visibleTrees(y, x, Direction.DOWN)
        val lt = visibleTrees(y, x, Direction.LEFT)
        val rt = visibleTrees(y, x, Direction.RIGHT)
        return up * dn * lt * rt
    }

    fun part2(input: List<String>) = input
        .mapIndexed { y, row ->
            row.indices.maxOf { x ->
                input.scenicScore(y, x)
            }
        }
        .maxOf { it }

    val input = readInput("Day${DAY}")
    println(part1(input))
    println(part2(input))
}
