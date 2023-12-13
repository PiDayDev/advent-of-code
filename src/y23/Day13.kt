package y23

private const val DAY = "13"

fun <T> List<T>.splitOn(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf(mutableListOf<T>())
    forEach { t ->
        if (predicate(t)) result.add(mutableListOf())
        else result.last().add(t)
    }
    return result.filter { it.isNotEmpty() }
}

fun List<String>.transposed(): List<String> = first()
    .indices
    .map { x ->
        map { row -> row[x] }.joinToString("")
    }

fun List<String>.reflectionLevels(): List<Int> {
    val levels = mutableListOf<Int>()
    (1..indices.last).forEach { i ->
        val top = take(i)
        val bottom = drop(i)
        val maxSize = top.size.coerceAtMost(bottom.size)
        val topFragment = top.takeLast(maxSize)
        val bottomFragment = bottom.take(maxSize).reversed()
        if (topFragment == bottomFragment) {
            levels += i
        }
    }
    return levels
}

fun Char.flip() = if (this == '#') '.' else '#'

fun List<String>.flip(x: Int, y: Int): List<String> = mapIndexed { currY, row ->
    when (currY) {
        y -> row.mapIndexed { currX, c -> if (currX == x) c.flip() else c }.joinToString("")
        else -> row
    }
}

fun List<String>.variations(): Sequence<List<String>> = asSequence()
    .flatMapIndexed { y, row ->
        row.mapIndexed { x, _ -> this.flip(x, y) }
    }

fun main() {
    fun score(rows: Int, cols: Int) = 100 * rows + cols

    fun part1(blocks: List<List<String>>): Int {
        val reflectedRows = blocks.flatMap { it.reflectionLevels() }.sum()
        val reflectedCols = blocks.flatMap { it.transposed().reflectionLevels() }.sum()
        return score(rows = reflectedRows, cols = reflectedCols)
    }

    fun scoreBlockPart2(block: List<String>): Int {
        val oldRow = block.reflectionLevels().firstOrNull() ?: 0
        val oldCol = block.transposed().reflectionLevels().firstOrNull() ?: 0

        return block
            .variations()
            .map {
                val rows: List<Int> = it.reflectionLevels()
                val cols: List<Int> = it.transposed().reflectionLevels()
                val newRow = rows.firstOrNull { r -> r != oldRow && r > 0 } ?: 0
                val newCol = cols.firstOrNull { c -> c != oldCol && c > 0 } ?: 0
                newRow to newCol
            }
            .distinct()
            .map { (rows, cols) ->
                score(rows, cols)
            }
            .sum()
    }

    fun part2(blocks: List<List<String>>): Int =
        blocks.sumOf(::scoreBlockPart2)

    val blocks = readInput("Day$DAY").splitOn { it.isBlank() }
    println(part1(blocks))
    println(part2(blocks))
}
