package y24

private const val DAY = "06"

fun main() {

    fun part1(state: State): Int =
        state.simulateGuard().distinctBy { it.position }.count()

    fun part2(state: State): Int {
        val candidates = state.simulateGuard().map { it.position }.toSet()
        return candidates.count {
            state.withNewObstruction(it).willLoop()
        }
    }

    val initialState = readInput("Day$DAY").toState()
    println(part1(initialState))
    println(part2(initialState))

}

private data class Guard(val position: Position, val direction: Direction) {
    fun turnRight() = copy(direction = direction.turnRight())
}

private data class State(
    private val width: Int,
    private val height: Int,
    private val obstructions: Set<Position>,
    private val guard: Guard,
    private val previousGuardStates: MutableSet<Guard> = mutableSetOf()
) {
    fun withNewObstruction(newObstruction: Position) =
        copy(obstructions = obstructions + newObstruction, previousGuardStates = mutableSetOf())

    fun simulateGuard(): Set<Guard> =
        generateSequence(this) { it.move() }
            .dropWhile { !it.isTerminal() }
            .first()
            .previousGuardStates

    fun willLoop(): Boolean =
        generateSequence(this) { it.move() }
            .dropWhile { !it.isTerminal() && !it.isLoop() }
            .first()
            .isLoop()

    private fun move(): State {
        val newGuard = when (val candidatePos = guard.position + guard.direction.movement) {
            in obstructions -> guard.turnRight()
            else -> guard.copy(position = candidatePos)
        }
        // Using (shared) mutable for performance reason
        previousGuardStates += guard
        return copy(guard = newGuard)
    }

    private fun isTerminal() = guard.position.run {
        x !in 0 until width || y !in 0 until height
    }

    private fun isLoop() = guard in previousGuardStates


}

private fun List<String>.toState(): State {
    val height = this.count()
    val width = first().count()
    val obstructions = mutableSetOf<Position>()
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
