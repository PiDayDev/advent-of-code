package y22

private const val DAY = "06"

fun main() {
    fun String.hasRepetitions() = toSet().size < length

    fun detectMarkerPacket(stream: String, length: Int): Int {
        val lengthOfInvalidPrefix = stream
            .windowed(length)
            .takeWhile { it.hasRepetitions() }
            .size
        return lengthOfInvalidPrefix + length
    }

    fun part1(input: String) = detectMarkerPacket(input, 4)

    fun part2(input: String) = detectMarkerPacket(input, 14)

    val input = readInput("Day$DAY").first()
    println(part1(input))
    println(part2(input))
}
