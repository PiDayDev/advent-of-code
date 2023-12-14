package y23

private const val DAY = "14"

data class RockyDish(
    val height: Int,
    val width: Int,
    val round: Set<Position>,
    val square: Set<Position>
) {
    fun moveTo(direction: Position): RockyDish {
        val yIndices = 0 until height
        val xIndices = 0 until width
        var prev = round
        var curr = round

        fun isValid(candidate: Position) =
            candidate !in prev && candidate !in square &&
                    candidate.x in xIndices && candidate.y in yIndices

        do {
            prev = curr
            curr = prev.map { pos ->
                generateSequence(pos + direction) { it + direction }
                    .takeWhile { isValid(it) }.lastOrNull() ?: pos
            }.toSet()
        } while (prev != curr)
        return copy(round = curr)
    }

    fun cycle(): RockyDish =
        moveTo(north).moveTo(west).moveTo(south).moveTo(east)

    fun totalLoad() = round.sumOf { height - it.y }
}

fun RockyDish(input: List<String>): RockyDish {
    val height = input.size
    val width = input.first().length
    val round = mutableListOf<Position>()
    val square = mutableListOf<Position>()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            val pos = Position(x, y)
            when (c) {
                'O' -> round += pos
                '#' -> square += pos
            }
        }
    }
    return RockyDish(height, width, round.toSet(), square.toSet())
}


fun main() {

    fun part1(input: List<String>): Int =
        RockyDish(input).moveTo(north).totalLoad()

    fun part2(input: List<String>): Int {
        val cache = mutableListOf<RockyDish>()

        var currDish = RockyDish(input)
        val last = 1000000000
        var index = 0
        while (index < last) {
            index++
            val nextDish = currDish.cycle()
            if (nextDish in cache) {
                val previousIndex = cache.lastIndexOf(nextDish)
                val period = index - previousIndex - 1
                val repeat = (last - index) / period
                if (repeat > 0) {
                    index += repeat * period
                }
            }
            cache.add(nextDish)
            currDish = nextDish
        }
        return currDish.totalLoad()
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
