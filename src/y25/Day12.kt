package y25

private const val DAY = "12"

private data class Shape(val rows: List<String>) {
    val filledTiles = rows.joinToString("").count { it == '#' }
}

private data class Region(val w: Int, val h: Int, val shapes: Map<Shape, Int>) {
    private val regionSize = w * h
    private val regionSquares3x3 = (w / 3) * (h / 3) * 3 * 3

    private val shapesFilledTiles = shapes.toList().sumOf { (t, n) -> t.filledTiles * n }
    private val shapesCount = shapes.values.sum()

    fun canFit() = when {
        shapesFilledTiles > regionSize -> false
        regionSquares3x3 >= shapesCount -> true
        else -> throw RuntimeException("too hard, sorry")
    }
}

fun main() {
    val input = readInput("Day$DAY")

    val shapes: List<Shape> = input
        .dropLastWhile { it.isNotBlank() }
        .chunked(5)
        .map { rows -> Shape(rows.filter { '#' in it || '.' in it }) }

    val regions = input
        .takeLastWhile { it.isNotBlank() }
        .map { row ->
            val (regionSize, shapeData) = row.split(": ")
            val (w, h) = regionSize.split("x").map { it.toInt() }
            val quantitiesByShape = shapeData.split(" ").map { it.toInt() }
            Region(w, h, shapes.zip(quantitiesByShape).toMap())
        }

    println(regions.count { it.canFit() })
}
