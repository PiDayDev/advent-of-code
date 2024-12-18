package y24

private const val DAY = "09"

private sealed class Block
private data class File(val id: Int) : Block()
private object Free : Block()

private data class MemoryBlock(val address: Int, val size: Int, val block: Block) {
    fun checksum(): Long = when (block) {
        is File -> block.id.toLong() * (address until address + size).sum()
        else -> 0
    }
}

fun main() {

    fun part1(input: String): Long {
        fun String.toExpandedMemory(): MutableList<Int?> {
            val memory = mutableListOf<Int?>()
            for (i in indices step 2) {
                val blockSize = this[i].digitToInt()
                val freeSize = getOrNull(i + 1)?.digitToInt() ?: 0
                repeat(blockSize) { memory += i / 2 }
                repeat(freeSize) { memory += null }
            }
            return memory
        }

        fun List<Int?>.checksum() =
            mapIndexed { i, v -> i.toLong() * (v ?: 0).toLong() }.sum()

        val memory = input.toExpandedMemory()
        var front = 0
        var back = memory.lastIndex
        while (true) {
            while (memory[front] != null) front++
            while (memory[back] == null) back--
            if (front >= back) break
            memory[front] = memory[back]
            memory[back] = null
        }
        return memory.checksum()
    }

    fun part2(input: String): Long {
        fun String.toMemoryBlocks(): MutableList<MemoryBlock> {
            val memoryBlocks = mutableListOf<MemoryBlock>()
            var currentIndex = 0
            for (i in indices step 2) {
                val blockSize = get(i).digitToInt()
                memoryBlocks += MemoryBlock(currentIndex, blockSize, File(i / 2))
                currentIndex += blockSize
                val freeSize = getOrNull(i + 1)?.digitToInt() ?: 0
                memoryBlocks += MemoryBlock(currentIndex, freeSize, Free)
                currentIndex += freeSize
            }
            return memoryBlocks
        }

        fun MutableList<MemoryBlock>.moveBack(id: Int) {
            val candidate = last { it.block is File && it.block.id == id }
            val container =
                firstOrNull { it.block is Free && it.address < candidate.address && it.size >= candidate.size }
            if (container == null) return
//        println("Move $candidate at the start of ${container.address}")

            // create a new copy at new position
            val replacedCandidate = candidate.copy(address = container.address)
            // create a new container that is farther to the right and shorter
            val replacedContainer = container.copy(
                address = container.address + candidate.size,
                size = container.size - candidate.size
            )
            // replace old elements with new elements
            remove(candidate)
            remove(container)
            add(replacedCandidate)
            add(replacedContainer)
            sortBy { it.address }
        }

        val memoryBlocks = input.toMemoryBlocks()
        val maxId = input.length / 2
        for (id in maxId downTo 0) {
            memoryBlocks.moveBack(id)
        }
        return memoryBlocks.sumOf { it.checksum() }
    }

    val input = readInput("Day$DAY").first()
    println(part1(input))
    println(part2(input))
}
