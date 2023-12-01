package y23

private const val DAY = "01"

fun main() {
    val input = readInput("Day$DAY")
    println(input.sumOf { it.toNumber() })
    println(input.sumOf { it.toNumberWithTextDigits() })
}

private fun String.toNumber(): Int {
    val digits = filter { it in "0123456789" }
    val first = digits.first()
    val last = digits.last()
    return first.digitToInt() * 10 + last.digitToInt()
}

private fun String.toNumberWithTextDigits(): Int {
    val firstDigit: Int = indices
        .asSequence()
        .map { drop(it) }
        .mapNotNull { it.matchingStartDigit() }
        .first()
    val lastDigit: Int = indices
        .asSequence()
        .map { dropLast(it) }
        .mapNotNull { it.matchingEndDigit() }
        .first()
    return firstDigit * 10 + lastDigit
}

private fun String.matchingStartDigit(): Int? = digitsMap
    .keys
    .firstOrNull { startsWith(it) }
    ?.let { digitsMap[it] }

private fun String.matchingEndDigit() = digitsMap
    .keys
    .firstOrNull { endsWith(it) }
    ?.let { digitsMap[it] }

private val digitsMap = mapOf(
    "one" to 1, "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7, "eight" to 8, "nine" to 9,
    "0" to 0, "1" to 1, "2" to 2, "3" to 3, "4" to 4, "5" to 5, "6" to 6, "7" to 7, "8" to 8, "9" to 9,
)
