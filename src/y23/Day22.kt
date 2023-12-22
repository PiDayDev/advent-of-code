package y23

import common.overlaps

private const val DAY = "22"

data class Brick(val xs: IntRange, val ys: IntRange, val zs: IntRange) {

    private fun fall(amount: Int = 1): Brick {
        val height = zs.last - zs.first
        val minZ = (zs.first - amount).coerceAtLeast(1)
        return copy(zs = minZ..minZ + height)
    }

    private infix fun isAbove(brick: Brick) = this !== brick &&
            zs.first > brick.zs.last &&
            xs overlaps brick.xs &&
            ys overlaps brick.ys

    infix fun supports(brick: Brick): Boolean =
        brick.isAbove(this) && brick.zs.first == 1 + this.zs.last

    fun fallOnto(bricks: Collection<Brick>): Brick {
        val zLimit = bricks.maxOf { brick ->
            when {
                this isAbove brick -> brick.zs.last
                else -> 0
            }
        }
        return fall(zs.first - (zLimit + 1))
    }

}

fun Collection<Brick>.fallToBottom(): Set<Brick> =
    generateSequence(toSet()) { set ->
        val next = set.map { it.fallOnto(set) }.toSet()
        if (next == set) null else next
    }.last()

fun String.toBrick(): Brick {
    val (a, b) = split("~")
    val (x0, y0, z0) = a.split(",").map { it.toInt() }
    val (x1, y1, z1) = b.split(",").map { it.toInt() }
    return Brick(x0..x1, y0..y1, z0..z1)
}

fun main() {
    val bricks = readInput("Day$DAY")
        .map { it.toBrick() }
        .fallToBottom()

    val brickToSupporters = bricks.associateWith { brick ->
        bricks.filter { other -> other supports brick }
    }

    fun part1(bricks: Collection<Brick>): Int {
        val essentialsCount = brickToSupporters.values.filter { it.size == 1 }.flatten().distinct().size
        return bricks.size - essentialsCount
    }

    fun part2(bricks: Collection<Brick>): Int = bricks
        .sumOf { brick ->
            val removedBricks = mutableSetOf(brick)
            var hasChanges = true
            while (hasChanges) {
                val unstable = brickToSupporters
                    .filterValues { supporters -> supporters.isNotEmpty() && removedBricks.containsAll(supporters) }
                    .keys
                hasChanges = removedBricks.addAll(unstable)
            }
            removedBricks.size - 1
        }

    println(part1(bricks))
    println(part2(bricks))
}

