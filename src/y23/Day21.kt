package y23

import java.math.BigInteger

private const val DAY = "21"

fun main() {

    fun dijkstra(input: List<String>, start: Position, max: Int): Map<Position, Int> {
        val distances = mutableMapOf<Position, Int>()
        var frontier = setOf(start)
        var distance = 0

        while (frontier.isNotEmpty() && distance <= max) {
            frontier.forEach { distances[it] = distance }
            frontier = frontier
                .asSequence()
                .flatMap { it.around() }
                .filterNot { it in distances }
                .filter { (x, y) -> (input.getOrNull(y)?.getOrNull(x) ?: '#') in "S." }
                .toSet()
            distance++
        }
        return distances
    }

    fun part1(input: List<String>, goal: Int): Int {
        val yStart = input.indexOfFirst { 'S' in it }
        val xStart = input[yStart].indexOf('S')

        val distances = dijkstra(input, Position(xStart, yStart), goal)

        return distances.values.count { it % 2 == goal % 2 }
    }

    fun expand(input: List<String>, gridToAddInEachDirection: Int): List<String> {
        val copy: List<String> = input.map { it.replace('S', '.') }
        return (-gridToAddInEachDirection..gridToAddInEachDirection).flatMap { y ->
            copy.mapIndexed { j, s ->
                s.repeat(gridToAddInEachDirection) + (if (y == 0) input[j] else s) + s.repeat(gridToAddInEachDirection)
            }
        }
    }

    fun part2(input: List<String>): BigInteger {
        // Note that:
        // - starting cell is in the center of a square grid, with dimension 131 x 131;
        // - there are no obstacles on the first row, last row, and the row of starting cell;
        // - the same is true for columns;
        // - so every cell on the grid borders can be reached in Manhattan distance
        //   (e.g. 65 for the middle point of each side, 131 for each corner);
        // - step goal 26_501_365 equals 65 [= distance from center to middle point of border] modulo 131 [= grid width].

        // When we tile multiple copies of the grid next to each other, confining tiles have different parity,
        //  meaning that each cell that is reached with an even number of steps in the starting grid can be reached
        //  with an odd number of steps in the grids directly above/below/left/right (and again with an even number if we move one grid farther).
        // See visualizations/Day21.png: green cells are reached with odd number of steps and yellow with even;
        //  grids with red stripes have the same parity of central grid, that has a "S" character in its center.

        // Given the above, after taking enough steps to cover a few copies of the grids, adding 262 steps will just
        //  add more copies of the same kind of grids, with the same step patterns.
        //  Since we are moving on a plane (2 dimensions), this suggests there should be a quadratic formula
        //  that gives the number of reached cells (y) given the number of steps (x), and that is valid for
        //   x = 65 + 262*n, with n>1 (for n = 1 we'd still be in the initial grid).

        // The formula is  y=ax²+bx+c  ==> we can compute y for three different (small) x by re-using part1,
        //  then plug in the values in the formula and solve the resulting system of 3 linear equations for a,b,c

        val expandedInput = expand(input, 6)
        println("\n Linear equation system")
        val (eq1, eq2, eq3) = (1..3).map { n ->
            val x = 65 + 262 * n
            val y = part1(input = expandedInput, goal = x)
            val equation = Equation(x * x, x, 1, y)
            println("($n) $equation")
            equation
        }

        println("\n Solving by elimination...")

        val eqA = (eq1 + eq3 - eq2 * 2).simplify()
        println("(A) $eqA".padEnd(40) + " | combination of (1),(2),(3) to eliminate b & c ==> find <a>")

        val eq12 = (eq1 - eq2).simplify()
        val eqB = (eq12 * eqA.c1 - eqA * eq12.c1).simplify()
        println("(B) $eqB".padEnd(40) + " | combination of (1),(2),(A) to eliminate a & c ==> find <b>")

        val eq1A = (eq1 * eqA.c1 - eqA * eq1.c1).simplify()
        val eqC = (eq1A * eqB.c2 - eqB * eq1A.c2).simplify()
        println("(C) $eqC".padEnd(40) + " | combination of (1),(A),(B) to eliminate a & b ==> find <c>")

        assert(eqA.c2 == 0L && eqA.c3 == 0L)
        assert(eqB.c1 == 0L && eqB.c3 == 0L)
        assert(eqC.c1 == 0L && eqC.c2 == 0L)
        assert(eqA.c1 == eqB.c2 && eqB.c2 == eqC.c3)

        val aNum = eqA.term.toBigInteger()
        val bNum = eqB.term.toBigInteger()
        val cNum = eqC.term.toBigInteger()
        val den = eqA.c1.toBigInteger()


        val steps = 26_501_365.toBigInteger()
        println("\n Resulting equation")
        println("y = ($aNum x² + $bNum x + $cNum) / $den")
        println("  = ($aNum * ${steps*steps} + $bNum * $steps + $cNum) / $den")
        return (aNum * steps * steps + bNum * steps + cNum) / den
    }

    val input = readInput("Day$DAY")
    println("PART 1 = ${part1(input, 64)}")
    println("PART 2 = ${part2(input)}")

}

private data class Equation(val c1: Long, val c2: Long, val c3: Long, val term: Long) {
    constructor(p: Int, q: Int, r: Int, s: Int) : this(p.toLong(), q.toLong(), r.toLong(), s.toLong())

    override fun toString(): String = "$c1 a + $c2 b + $c3 c = $term"

    operator fun times(n: Long) = Equation(c1 * n, c2 * n, c3 * n, term * n)
    operator fun div(n: Long) = Equation(c1 / n, c2 / n, c3 / n, term / n)
    operator fun unaryMinus() = this * -1
    operator fun plus(e: Equation) = Equation(c1 + e.c1, c2 + e.c2, c3 + e.c3, term + e.term)
    operator fun minus(e: Equation) = this + -e

    private tailrec fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

    fun simplify(): Equation {
        val g = gcd(gcd(c1, c2), gcd(c3, term))
        return if (g == 0L) this else this / g
    }
}

