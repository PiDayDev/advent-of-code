package y23

private const val DAY = "15"

fun main() {
    fun String.hash(): Int =
        fold(0) { currentValue, character ->
            ((currentValue + character.code) * 17) % 256
        }

    fun part1(input: List<String>): Int =
        input.sumOf(String::hash)

    fun part2(input: List<String>): Int {
        val boxes = Array(256) { mutableListOf<Lens>() }

        fun remove(label: String) {
            val address = label.hash()
            boxes[address].removeIf { it.label == label }
        }

        fun add(lens: Lens) {
            val address = lens.label.hash()
            val box = boxes[address]
            val index = box.indexOfFirst { it.label == lens.label }
            if (index >= 0) {
                box[index] = lens
            } else {
                box.add(lens)
            }
        }

        input.forEach { op ->
            val label = op.substringBefore("-").substringBefore("=")
            if (op.endsWith("-")) {
                remove(label)
            } else {
                val focalLength = op.substringAfter("=").toInt()
                add(Lens(label, focalLength))
            }
        }

        return boxes
            .mapIndexed { index, lenses: List<Lens> ->
                val focusingPower = lenses
                    .mapIndexed { slot, lens -> (slot + 1) * lens.focalLength }
                    .sum()
                (index + 1) * focusingPower
            }
            .sum()
    }

    val input = readInput("Day$DAY").joinToString("").split(",")
    println(part1(input))
    println(part2(input))
}

data class Lens(val label: String, val focalLength: Int)
