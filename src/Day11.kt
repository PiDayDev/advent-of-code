import java.math.BigInteger
import java.math.BigInteger.ONE
import java.math.BigInteger.ZERO

private class Monkey(
    val divisor: BigInteger,
    val operation: (BigInteger) -> BigInteger,
) {
    val items = mutableListOf<BigInteger>()
    var onDivisible: Monkey? = null
    var onElse: Monkey? = null

    private var counter: BigInteger = ZERO

    fun countInspected() = counter

    fun hasNext() = items.isNotEmpty()
    fun processNext(lcm: BigInteger, relief: (BigInteger) -> BigInteger) {
        val item = items.removeFirst().let(operation).let(relief)
        val recipient = when (item.remainder(divisor)) {
            ZERO -> onDivisible
            else -> onElse
        }
        recipient!!.items.add(item.remainder(lcm))
        counter++
    }

    fun takeTurn(lcm: BigInteger, relief: (BigInteger) -> BigInteger) {
        while (hasNext())
            processNext(lcm, relief)
    }
}

private fun List<Monkey>.monkeyBusiness() =
    map { it.countInspected() }.sortedDescending().let { (a, b) -> a * b }

private fun List<Monkey>.lowestCommonMultiplier() =
    map { it.divisor }.fold(ONE) { a, b -> a * b }

fun main() {
    fun part1(monkeys: List<Monkey>): BigInteger {
        val lcm = monkeys.lowestCommonMultiplier()
        repeat(20) {
            monkeys.forEach { monkey -> monkey.takeTurn(lcm) { it / 3.b } }
        }
        return monkeys.monkeyBusiness()
    }

    fun part2(monkeys: List<Monkey>): BigInteger {
        val lcm = monkeys.lowestCommonMultiplier()
        repeat(10000) {
            monkeys.forEach { monkey -> monkey.takeTurn(lcm) { it } }
        }
        return monkeys.monkeyBusiness()
    }

    fun monkeys(): List<Monkey> {
        val m0 = Monkey(2.b) { it * 11.b }
        val m1 = Monkey(7.b) { it * it }
        val m2 = Monkey(13.b) { it + 1.b }
        val m3 = Monkey(3.b) { it + 2.b }
        val m4 = Monkey(19.b) { it * 13.b }
        val m5 = Monkey(17.b) { it + 5.b }
        val m6 = Monkey(11.b) { it + 6.b }
        val m7 = Monkey(5.b) { it + 7.b }

        m0.onDivisible = m4
        m1.onDivisible = m3
        m2.onDivisible = m4
        m3.onDivisible = m6
        m4.onDivisible = m1
        m5.onDivisible = m2
        m6.onDivisible = m2
        m7.onDivisible = m1

        m0.onElse = m7
        m1.onElse = m6
        m2.onElse = m0
        m3.onElse = m5
        m4.onElse = m7
        m5.onElse = m0
        m6.onElse = m5
        m7.onElse = m3

        m0.items.addAll(listOf(84, 66, 62, 69, 88, 91, 91).map { it.b })
        m1.items.addAll(listOf(98, 50, 76, 99).map { it.b })
        m2.items.addAll(listOf(72, 56, 94).map { it.b })
        m3.items.addAll(listOf(55, 88, 90, 77, 60, 67).map { it.b })
        m4.items.addAll(listOf(69, 72, 63, 60, 72, 52, 63, 78).map { it.b })
        m5.items.addAll(listOf(89, 73).map { it.b })
        m6.items.addAll(listOf(78, 68, 98, 88, 66).map { it.b })
        m7.items.addAll(listOf(70).map { it.b })

        return listOf(m0, m1, m2, m3, m4, m5, m6, m7)
    }

    println(part1(monkeys()))
    println(part2(monkeys()))
}
