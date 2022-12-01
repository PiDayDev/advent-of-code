fun main() {

    fun headAndTail(input: List<String>): Pair<List<String>, List<String>> {
        val head = input.takeWhile { it.isNotBlank() }
        val tail = input.dropWhile { it.isNotBlank() }.drop(1)
        return head to tail
    }

    fun splitOnSpaces(input: List<String>): List<List<String>> {
        val (head, tail) = headAndTail(input)
        if (tail.isEmpty())
            return listOf(head)
        return splitOnSpaces(tail).plus(listOf(head))
    }

    fun toCalories(input: List<String>): List<Int> {
        val groups = splitOnSpaces(input)
        val calories = groups.map { group: List<String> ->
            group.sumOf { it.toInt() }
        }
        return calories
    }

    fun part1(input: List<String>) =
        toCalories(input).maxOf { it }

    fun part2(input: List<String>): Int {
        val calories = toCalories(input).sortedDescending()
        return calories.take(3).sum()
    }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
