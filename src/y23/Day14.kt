package y23

private const val DAY = "14"

data class Platform(
    val height: Int,
    val width: Int,
    val round: Set<Position>,
    val square: Set<Position>
) {
    fun moveTo(direction: Position): Platform {
        var prev = round
        var curr = round

        fun isValid(candidate: Position) =
            candidate.x in 0 until width &&
            candidate.y in 0 until height &&
            candidate !in square &&
            candidate !in prev

        do {
            prev = curr
            curr = prev
                .map { pos -> (pos + direction).takeIf(::isValid) ?: pos }
                .toSet()
        } while (prev != curr)
        return copy(round = curr)
    }

    fun cycle(): Platform =
        moveTo(north).moveTo(west).moveTo(south).moveTo(east)

    fun totalLoad() = round.sumOf { height - it.y }
}

fun Platform(input: List<String>): Platform {
    val height = input.size
    val width = input.first().length
    val round = mutableSetOf<Position>()
    val square = mutableSetOf<Position>()
    input.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            val pos = Position(x, y)
            when (c) {
                'O' -> round += pos
                '#' -> square += pos
            }
        }
    }
    return Platform(height, width, round, square)
}


fun main() {

    fun part1(input: List<String>): Int =
        Platform(input).moveTo(north).totalLoad()

    fun part2(input: List<String>): Int {
        val cache = mutableListOf<Platform>()

        var currPlatform = Platform(input)
        val last = 1_000_000_000
        var index = 0
        while (index < last) {
            val nextPlatform = currPlatform.cycle()
            if (nextPlatform in cache) {
                val previousIndex = cache.lastIndexOf(nextPlatform)
                val period = index - previousIndex
                val repeat = (last - index) / period
                if (repeat > 0) {
                    index += repeat * period
                }
            }
            cache.add(nextPlatform)
            currPlatform = nextPlatform
            index++
        }
        return currPlatform.totalLoad()
    }

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
