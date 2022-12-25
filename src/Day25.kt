import kotlin.math.pow

private const val DAY = 25

private fun Char.toSnafuDigit() = when (this) {
    '=' -> -2
    '-' -> -1
    else -> digitToInt()
}

class Snafu(val value: Long) {
    constructor(snafuString: String) : this(
        snafuString.reversed()
            .mapIndexed { j, c -> 5.0.pow(j).toLong() * c.toSnafuDigit() }
            .sum()
    )

    override fun toString(): String {
        val base5 = value.toString(5)
        println(base5)
        val b = StringBuffer()

        var riporto = 0
        base5.reversed().forEach { c ->
            when (val k = c.digitToInt() + riporto) {
                0, 1, 2 -> {
                    b.append(k)
                    riporto = 0
                }

                3 -> {
                    b.append("=")
                    riporto = +1
                }

                4 -> {
                    b.append("-")
                    riporto = +1
                }

                5 -> {
                    b.append(0)
                    riporto = +1
                }

                else -> check(false) { "$c should not happen" }
            }
        }
        if (riporto == 1) b.append(1)
        return b.reverse().toString()
    }


}

fun main() {
    fun part1(input: List<String>): String {
        val result = input.map { Snafu(it) }.sumOf { it.value }
        return Snafu(result).toString()
    }

    val input = readInput("Day${DAY}")
    println(part1(input))
}
