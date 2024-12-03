package y24

private const val DAY = "99"

fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    /* try {
         val testInput = readInput("Day${DAY}_test")
         check(part1(testInput) == 1)
     } catch (e: java.io.FileNotFoundException) {
         // no tests
     }*/

    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
