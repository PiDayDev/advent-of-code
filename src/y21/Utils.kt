package y21

import java.io.File
import java.util.*

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/y21", "$name.txt").readLines()

/*
ADVANCED DATA STRUCTURES
 */
interface PriorityQ<T : Comparable<T>?> {
    fun add(element: T): Boolean
    fun updatePriority(oldElement: T, newElement: T): Boolean
    fun remove(element: T): Boolean
    fun top(): T?
    operator fun contains(element: T): Boolean
    val size: Int
    fun isEmpty(): Boolean
    fun isNotEmpty() = !isEmpty()
}

class MyQ<T : Comparable<T>?>(private val queue: PriorityQueue<T> = PriorityQueue<T>()) :
    PriorityQ<T>, Queue<T> by queue {

    override fun updatePriority(oldElement: T, newElement: T): Boolean {
        val wasPresent = queue.removeIf { it == oldElement }
        if (wasPresent) queue.add(newElement)
        return wasPresent
    }

    override fun top(): T? = poll()

}


