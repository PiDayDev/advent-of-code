package y22

import java.util.*

private const val DAY = "05"

fun main() {

    fun part1(cargo: Cargo, moves: List<Move>): String {
        moves.forEach(cargo::moveOneByOne)
        return cargo.tops()
    }

    fun part2(cargo: Cargo, moves: List<Move>): String {
        moves.forEach(cargo::moveManyAtOnce)
        return cargo.tops()
    }

    val input = readInput("Day$DAY")

    val startingCargo = input.takeWhile { it.isNotBlank() }

    val moves = input.takeLastWhile { it.isNotBlank() }.map { it.toMove() }

    println(part1(startingCargo.toCargo(), moves))
    println(part2(startingCargo.toCargo(), moves))
}

private fun String.sep() = split(' ')
private fun String.toMove(): Move {
    val (quantity, source, target) = sep().chunked(2).map { (_, n) -> n.toInt() }
    return Move(quantity, source, target)
}

private class Move(val quantity: Int, val source: Int, val target: Int)

private class Cargo(val stacks: Map<Int, Deque<String>>) {
    fun moveOneByOne(move: Move) {
        repeat(move.quantity) {
            stacks[move.target]?.push(stacks[move.source]?.pop())
        }
    }

    fun moveManyAtOnce(move: Move) {
        val origin = stacks[move.source]!!
        val destination = stacks[move.target]!!
        val moving = mutableListOf<String>()
        repeat(move.quantity) {
            moving.add(origin.pop())
        }
        while (moving.isNotEmpty()) {
            destination.push(moving.removeLast())
        }

    }

    fun tops() = stacks.toSortedMap().values.joinToString("") { it.peek() }
}

private fun List<String>.toCargo(): Cargo {
    val ids = last().split(" +".toRegex()).filter { it.isNotBlank() }.map { it.toInt() }

    fun String.itemForStack(id: Int) = getOrNull(id * 4 - 3)?.toString() ?: " "

    val map = ids.associateWith { LinkedList<String>() }.toMutableMap()
    dropLast(1).forEach { row ->
        ids.forEach { id ->
            val item = row.itemForStack(id)
            if (item.isNotBlank())
                map[id]?.add(item)
        }
    }

    return Cargo(map)
}
