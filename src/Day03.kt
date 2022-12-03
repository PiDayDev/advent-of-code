fun main() {

    val alphabet = listOf(' ') + ('a'..'z').toList() + ('A'..'Z').toList()

    fun Char.priority() = alphabet.indexOf(this)

    fun String.halves() = take(length / 2) to drop(length / 2)

    fun String.repetition() = halves()
        .let { (left, right) -> left.first { it in right } }

    fun part1(input: List<String>) =
        input.sumOf { it.repetition().priority() }

    fun part2(input: List<String>) = input
        .chunked(3)
        .sumOf { (first, second, third) ->
            first.find { it in second && it in third }!!.priority()
        }

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
