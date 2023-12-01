package y22

private const val DAY = 10

fun main() {
    fun computeSignal(input: List<String>): Map<Int, Int> {
        var t = 0
        var x = 1
        val signal = mutableMapOf(0 to 1)
        input.forEach {
            t++
            signal[t] = x
            if (it.startsWith("a")) {
                t++
                x += it.substringAfter("addx ").toInt()
                signal[t] = x
            }
        }
        return signal
    }

    fun part1(signal: Map<Int, Int>) =
        (20..220 step 40)
            .sumOf { it * signal[it - 1]!! }

    fun part2(signal: Map<Int, Int>) =
        (0 until 6).map { y ->
            (0 until 40).joinToString("") { x ->
                val t = y * 40 + x
                when {
                    signal[t] in x - 1..x + 1 -> "██"
                    else -> "  "
                }
            }
        }

    val input = readInput("Day$DAY")
    val signal = computeSignal(input)

    println(part1(signal))
    println(part2(signal).joinToString("\n"))
}
