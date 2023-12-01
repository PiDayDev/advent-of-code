package y22

private enum class RPS(val value: Int) {
    Rock(1), Paper(2), Scissors(3);

    infix fun beats(other: RPS) =
        (value - other.value + 3) % 3 == 1

    infix fun withStrategy(strategy: String) = when (strategy) {
        "X" -> /* lose */ RPS.values().first { me -> this beats me }
        "Y" -> /* draw */ this
        "Z" -> /* win  */ RPS.values().first { me -> me beats this }
        else -> throw IllegalArgumentException()
    }
}

private fun String.toRps(): RPS = when (this) {
    "A", "X" -> RPS.Rock
    "B", "Y" -> RPS.Paper
    "C", "Z" -> RPS.Scissors
    else -> throw IllegalArgumentException()
}

private fun String.toMatch1() = split(" ")
    .map { it.toRps() }
    .let { (p, q) -> p to q }

private fun String.toMatch2() = split(" ")
    .let { (them, strategy) ->
        val theirPlay = them.toRps()
        val myPlay = theirPlay withStrategy strategy
        theirPlay to myPlay
    }


private fun Pair<RPS, RPS>.resultScore() =
    if (first beats second) 0
    else if (second beats first) 6
    else 3

private fun Pair<RPS, RPS>.score() =
    second.value + resultScore()

fun main() {

    fun part1(input: List<String>) = input
        .map { it.toMatch1() }
        .sumOf { it.score() }

    fun part2(input: List<String>) = input
        .map { it.toMatch2() }
        .sumOf { it.score() }

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
