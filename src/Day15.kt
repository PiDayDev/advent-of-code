import kotlin.math.absoluteValue

private const val DAY = 15

private class Sensor(val sx: Long, val sy: Long, val bx: Long, val by: Long) {
    fun distance() = (sx - bx).absoluteValue + (sy - by).absoluteValue
    fun xRange(y: Long): LongRange {
        val delta = distance() - (sy - y).absoluteValue
        return when {
            delta >= 0 -> sx - delta..sx + delta
            else -> LongRange.EMPTY
        }
    }

    override fun toString() = "S($sx,$sy) 👁 B($bx,$by)"
}

fun main() {
    fun String.parse(): Sensor {
        val sx = substringAfter("Sensor at x=").substringBefore(", y=").toLong()
        val sy = substringAfter(", y=").substringBefore(": closest beacon is at x=").toLong()
        val bx = substringAfter(": closest beacon is at x=").substringBefore(", y=").toLong()
        val by = substringAfter(": closest beacon is at x=").substringAfter(", y=").toLong()
        return Sensor(sx, sy, bx, by)
    }

    fun part1(sensors: List<Sensor>, row: Long): Long {
        val ranges = sensors.map { it.xRange(row) }.union()
        val totalRangeSize = ranges.sumOf { it.endInclusive - it.start + 1 }

        val beaconsOnRow = sensors.filter { it.by == row }.map { it.bx }.toSet()
        val beaconsInRange = beaconsOnRow.count { ranges.any { range -> it in range } }

        return totalRangeSize - beaconsInRange.toLong()
    }

    /** Warning: low-performance method */
    fun part2(sensors: List<Sensor>): Long {
        fun tuning(x: Long, y: Long) = x * 4_000_000L + y
        val domain: LongRange = 0L..4_000_000L
        domain.forEach { row ->
            val ranges = sensors
                .flatMap { it.xRange(row) intersection domain }
                .union()
            val firstRange = ranges.first()
            when {
                ranges.size > 1 ->
                    return tuning(x = firstRange.endInclusive + 1, y = row)

                firstRange.start != domain.first ->
                    return tuning(x = firstRange.start, y = row)

                firstRange.endInclusive != domain.last ->
                    return tuning(x = firstRange.endInclusive, y = row)
            }
        }
        return -1L
    }

    val sensors = readInput("Day${DAY}").map { it.parse() }

    println(part1(sensors, 2_000_000L))
    println(part2(sensors))
}
