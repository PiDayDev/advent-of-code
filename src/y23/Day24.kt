package y23

private const val DAY = "24"

private data class Vector2D(val x: Double, val y: Double) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
    operator fun unaryMinus() = Vector2D(-x, -y)
    operator fun plus(v: Vector2D) = Vector2D(x + v.x, y + v.y)
    operator fun times(k: Double) = Vector2D(x * k, y * k)
}

private data class Hail2D(val position: Vector2D, val velocity: Vector2D) {
    private fun atTime(t: Double): Vector2D = velocity * t + position

    fun intersection(other: Hail2D): Intersection2D {
        val (x12, y12) = -velocity
        val (x34, y34) = -other.velocity
        val (x13, y13) = -other.position + position

        val tNumerator = x13 * y34 - y13 * x34
        val uNumerator = x13 * y12 - y13 * x12
        val crossProduct = x12 * y34 - y12 * x34

        // when crossProduct is 0, t & u are infinite i.e. outside range
        val t = tNumerator / crossProduct
        val u = uNumerator / crossProduct

        return Intersection2D(point = atTime(t), t = t, u = u)
    }

}

private data class Intersection2D(val point: Vector2D, val t: Double, val u: Double)

private data class Hail3D(val px: Long, val py: Long, val pz: Long, val vx: Long, val vy: Long, val vz: Long) {
    fun atTime(t: Long) = copy(px = px + t * vx, py = py + t * vy, pz = pz + t * vz)
}

private val coordinateDelimiter = Regex(" *, *")
private val partDelimiter = Regex(" *@ *")

private fun toHail3D(row: String): Hail3D {
    val (p, v) = row.split(partDelimiter)
    val position = p.split(coordinateDelimiter).map { it.toLong() }
    val velocity = v.split(coordinateDelimiter).map { it.toLong() }
    val (px, py, pz) = position
    val (vx, vy, vz) = velocity
    return Hail3D(px, py, pz, vx, vy, vz)
}

fun main() {

    fun part1(input: List<String>): Int {
        val hails = input
            .map(::toHail3D)
            .map { Hail2D(Vector2D(it.px, it.py), Vector2D(it.vx, it.vy)) }

        val testRange: ClosedRange<Double> = 2e14..4e14

        return hails
            .mapIndexed { i, hail1 ->
                hails
                    .drop(i + 1)
                    .map { hail2 -> hail2.intersection(hail1) }
                    .count { it.t > 0.0 && it.u > 0.0 && it.point.x in testRange && it.point.y in testRange }
            }
            .sum()
    }

    fun part2(input: List<String>): Long {
        val lines = input.map(::toHail3D)

        // Find the two lines that IN MY INPUT share the same pz and vz
        val groupOfMatchingLines = lines.groupBy { it.pz to it.vz }.filterValues { it.size > 1 }.values.first()

        /*
         Same pz and vz ==> same of the rock
         Let pr,vr be initial z position and z velocity of the rock; there must be DISTINCT t1 and t2 such that
          (line 1) pr + t1 * vr = pz + t1 * vz
          (line 2) pr + t2 * vr = pz + t2 * vz
          ==> the difference implies (t1-t2) * vr = (t1-t2) * vz
          ==> since t1!=t2, divide by non-zero t1-t2 ==> vr = vz
          ==> substitute in either equation to get pr = pz
         */
        val zLine = groupOfMatchingLines.first()
        val vzR = zLine.vz
        val pzR = zLine.pz
        println("vzR = $vzR and pzR = $pzR")

        val xEquations = mutableListOf<List<Long>>()
        val yEquations = mutableListOf<List<Long>>()
        lines
            .shuffled()
            .filter { it.vz != vzR && it.pz != pzR }
            // pzR + t * vzR = pzL + t * vzL ==> t = (pzL - pzR) / (vzR - vzL)
            .filter { line -> (line.pz - pzR) % (vzR - line.vz) == 0L }
            .take(2)
            .forEach { line ->
                val t = (line.pz - pzR) / (vzR - line.vz)
                val (pxL, pyL) = line.atTime(t)
                // pR + vR * t = pL
                xEquations.add(listOf(1, t, pxL))
                yEquations.add(listOf(1, t, pyL))
            }
        println()

        fun toEquationString(coefficients: List<Long>, axis: String): String {
            val (pCoeff, vCoeff, term) = coefficients
            val pTerm = if (pCoeff == 0L) "" else "$pCoeff p${axis}R + "
            return "$pTerm$vCoeff * v${axis}R = $term"
        }

        fun solveEquationSystem(axis: String, eq1: List<Long>, eq2: List<Long>): Pair<Long, Long> {
            println(toEquationString(eq1, axis))
            println(toEquationString(eq2, axis))
            val reduced = eq1.zip(eq2) { a, b -> a - b }
            val (pCoeff, vCoeff, term) = reduced
            check(pCoeff == 0L)
            val vR = term / vCoeff
            val pR = (eq1[2] - vR * eq1[1]) / eq1[0]
            println("${toEquationString(reduced, axis)} => v${axis}R = $vR and p${axis}R = $pR")
            println()
            return vR to pR
        }

        val (_, pxR) = solveEquationSystem("x", xEquations.first(), xEquations.last())
        val (_, pyR) = solveEquationSystem("y", yEquations.first(), yEquations.last())

        return pxR + pyR + pzR
    }

    val input = readInput("Day$DAY")
    println("PART 1 = ${part1(input)}")
    println("\n${"=".repeat(120)}\n")
    println("PART 2 = ${part2(input)}")
}

