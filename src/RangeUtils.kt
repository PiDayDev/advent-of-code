operator fun <T : Comparable<T>> ClosedRange<T>.contains(other: ClosedRange<T>) =
    other.start in this && other.endInclusive in this

infix fun <T : Comparable<T>> ClosedRange<T>.overlaps(other: ClosedRange<T>) =
    other.start in this || other.endInclusive in this || start in other || endInclusive in other

infix fun <T : Comparable<T>> ClosedRange<T>.union(other: ClosedRange<T>): List<ClosedRange<T>> =
    when {
        this in other -> listOf(other)
        other in this -> listOf(this)
        this overlaps other -> {
            val min = if (start < other.start) start else other.start
            val max = if (endInclusive > other.endInclusive) endInclusive else other.endInclusive
            listOf(min..max)
        }

        else -> listOf(this, other).sortedBy { it.start }
    }

infix fun <T : Comparable<T>> ClosedRange<T>.intersection(other: ClosedRange<T>): List<ClosedRange<T>> =
    when {
        this in other -> listOf(this)
        other in this -> listOf(other)
        this overlaps other -> {
            val min = if (start > other.start) start else other.start
            val max = if (endInclusive < other.endInclusive) endInclusive else other.endInclusive
            listOf(min..max)
        }

        else -> emptyList()
    }

fun <T : Comparable<T>> List<ClosedRange<T>>.union() =
    reduceWith(ClosedRange<T>::union)

fun <T : Comparable<T>> List<ClosedRange<T>>.intersection() =
    reduceWith(ClosedRange<T>::intersection)

private fun <T : Comparable<T>> List<ClosedRange<T>>.reduceWith(
    function: ClosedRange<T>.(ClosedRange<T>) -> List<ClosedRange<T>>
): List<ClosedRange<T>> {
    var k = this
    var z = k.recursionStep(function)
    while (z.size != k.size) {
        k = z
        z = k.recursionStep(function)
    }
    return z
}

private fun <T : Comparable<T>> List<ClosedRange<T>>.recursionStep(
    function: ClosedRange<T>.(ClosedRange<T>) -> List<ClosedRange<T>>
) =
    drop(1).fold(take(1)) { acc: List<ClosedRange<T>>, curr: ClosedRange<T> ->
        acc.flatMap { it.function(curr) }.distinct()
    }
