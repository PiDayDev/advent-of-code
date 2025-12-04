package y25

private const val DAY = "04"
private const val ROLL = '@'

fun main() {
    fun parse(input: List<String>): Set<Position> {
        val rolls = mutableSetOf<Position>()
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                if (c == ROLL)
                    rolls += Position(x, y)
            }
        }
        return rolls
    }

    infix fun Position.isAccessibleIn(set: Set<Position>) =
        around8().count { it in set } < 4

    fun part1(positions: Set<Position>): Int =
        positions.count { it isAccessibleIn positions }

    fun part2(input: Set<Position>): Int {
        val residual = input.toMutableSet()
        val removed = mutableSetOf<Position>()
        while (true) {
            val removable = residual.filter { it isAccessibleIn residual }
            if (removable.isEmpty()) break
            removed += removable
            residual -= removable.toSet()
        }
        return removed.size
    }

    val input = parse(readInput("Day$DAY"))
    println(part1(input))
    println(part2(input))
}
