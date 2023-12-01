package y22

private const val DAY = 20

private class CryptoToken(val value: Long) {
    override fun toString() = "$value"
    override fun equals(other: Any?) = this === other
    override fun hashCode() = value.hashCode()
}

private fun List<CryptoToken>.shift(token: CryptoToken): List<CryptoToken> {
    val value = token.value
    if (value == 0L)
        return this
    val currIndex = indexOf(token)

    val pre = take(currIndex)
    val post = drop(currIndex + 1)

    val next = (post + pre).toMutableList()
    val nextIndex = ((value % next.size).toInt() + next.size) % next.size
    next.add(nextIndex, token)
    return next
}

const val DAY_20_PART_2_DECRYPTION_KEY = 811589153L

fun main() {
    fun decrypt(input: List<Long>, decryptionKey: Long = 1, rounds: Int = 1): Long {
        val tokens = input.map { CryptoToken(it * decryptionKey) }
        var mix = tokens
        repeat(rounds) {
            tokens.forEach {
                mix = mix.shift(it)
            }
        }
        val i0 = mix.indexOfFirst { it.value == 0L }
        val elements = (1..3).map { mix[(i0 + 1000 * it) % mix.size] }
        return elements.sumOf { it.value }
    }

    fun part1(input: List<Long>) = decrypt(input)

    fun part2(input: List<Long>) = decrypt(input, DAY_20_PART_2_DECRYPTION_KEY, 10)

    val input = readInput("Day$DAY").map { it.toLong() }
    println(part1(input))
    println(part2(input))
}
