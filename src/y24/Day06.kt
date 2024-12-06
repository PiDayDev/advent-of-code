package y24

private const val DAY = "06"

fun main() {
    fun part1(initialState: State): Int {
        val finalState = generateSequence(initialState) {
            if (it.isTerminal()) null else it.move()
        }.last()
        return finalState.previous.distinctBy { it.position }.count()
    }

    fun willLoop(state: State) = generateSequence(state) {
        if (it.isTerminal() || it.isLoop()) null else it.move()
    }.last().isLoop()

    /* FIXME DAMIANO low performance */
    fun part2(state: State): Int {
        var counter = 0
        (0 until state.height).forEach { y ->
            (0 until state.width).forEach { x ->
                val candidate = Position(x, y)
                if (candidate !in state.obstructions) {
                    val newState = state.copy(obstructions = state.obstructions + candidate)
                    if (willLoop(newState)) counter++
                }
            }
        }
        return counter
    }

    val initialState = readInput("Day$DAY").toState()
    println(part1(initialState))
    println(part2(initialState))
}

private data class Guard(val position: Position, val direction: Direction) {
    fun turnRight() = copy(direction = direction.turnRight())
}

private data class State(
    val width: Int,
    val height: Int,
    val obstructions: List<Position>,
    val guard: Guard,
    val previous: List<Guard> = emptyList()
) {
    fun isTerminal() = guard.position.x !in 0 until width ||
            guard.position.y !in 0 until height

    fun isLoop() = guard in previous

    fun move(): State {
        val newGuard = when (val candidatePos = guard.position + guard.direction.movement) {
            in obstructions -> guard.turnRight()
            else -> guard.copy(position = candidatePos)
        }
        return copy(guard = newGuard, previous = previous + guard)
    }
}

private fun List<String>.toState(): State {
    val height = this.count()
    val width = first().count()
    val obstructions = mutableListOf<Position>()
    val guardPositions = mutableListOf<Position>()
    forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            when (c) {
                '#' -> obstructions += Position(x, y)
                '^' -> guardPositions += Position(x, y)
            }
        }
    }
    return State(width, height, obstructions, Guard(guardPositions.first(), Direction.N))
}
