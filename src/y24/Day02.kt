package y24

private const val DAY = "02"

fun main() {

    fun part1(reports: List<List<Int>>): Int =
        reports.count {
            it.isSafe()
        }

    fun part2(reports: List<List<Int>>): Int =
        reports.count {
            it.isSafe() || it.canBeMadeSafe()
        }

    fun parseReports(input: List<String>): List<List<Int>> = input.map {
        it.split(" ").map { e -> e.toInt() }
    }


    val input = readInput("Day$DAY")
    val reports = parseReports(input)
    println(part1(reports))
    println(part2(reports))
}

private fun List<Int>.isSafe(): Boolean {
    val pairs = windowed(2) { (a, b) -> a - b }
    return pairs.all { it in 1..3 } || pairs.all { -it in 1..3 }
}


private fun List<Int>.canBeMadeSafe(): Boolean =
    indices.any {
        (take(it) + drop(it + 1)).isSafe()
    }

