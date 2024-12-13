package y24

import java.math.BigInteger

private const val DAY = "13"

fun main() {
    val costA = 3.toBigInteger()
    val costB = 1.toBigInteger()
    val clawMachines = readInput("Day$DAY").toClaws()

    fun solve(claws: List<Claw>, increment: BigInteger) = claws
        .mapNotNull { it.solveWithPrizeCoordinateIncrementedBy(increment) }
        .sumOf { (a, b) -> a * costA + b * costB }

    println(solve(clawMachines, BigInteger.ZERO))
    println(solve(clawMachines, 10000000000000L.toBigInteger()))
}


private fun List<String>.toClaws(): List<Claw> {
    fun String.toPosition() = Position(
        x = substringAfter("X").substringBefore(",").removePrefix("=").toInt(),
        y = substringAfter("Y").removePrefix("=").toInt()
    )

    return chunked(4).map { (a, b, p) ->
        Claw(a.toPosition(), b.toPosition(), p.toPosition())
    }
}

private data class Claw(val a: Position, val b: Position, val prize: Position) {
    // Solve equation system with Cramer's method
    //     |xa xb| * |a| =  |xp|
    //     |ya yb|   |b|    |yp|
    fun solveWithPrizeCoordinateIncrementedBy(increment: BigInteger): Pair<BigInteger, BigInteger>? {
        val xp = prize.x.toBigInteger() + increment
        val yp = prize.y.toBigInteger() + increment
        val xa = a.x.toBigInteger()
        val ya = a.y.toBigInteger()
        val xb = b.x.toBigInteger()
        val yb = b.y.toBigInteger()
        val denominator = xa * yb - xb * ya
        val numeratorA = xp * yb - xb * yp
        val numeratorB = xa * yp - xp * ya
        return when {
            denominator == BigInteger.ZERO -> null
            numeratorA % denominator != BigInteger.ZERO -> null
            numeratorB % denominator != BigInteger.ZERO -> null
            else -> numeratorA / denominator to numeratorB / denominator
        }
    }

}
