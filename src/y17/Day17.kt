package y17

private const val steps = 363

private class Node(val item: Int) {
    var next: Node = this
    fun append(newNode: Node) {
        newNode.next = next
        next = newNode
    }
}

fun main() {

    fun shortCircuit(steps: Int, loopCount: Int, findAfter: Int): Int {
        var node = Node(0)
        var t = System.currentTimeMillis()
        for (currentSize in 1..loopCount) {
            repeat(steps) {
                node = node.next
            }
            Node(currentSize).let { new ->
                node.append(new)
                node = new
            }
            if (currentSize % 1_000_000 == 0) {
                println("${System.currentTimeMillis() - t}ms #### Loop $currentSize of $loopCount")
                t = System.currentTimeMillis()
            }
        }
        val goal = generateSequence(node) { it.next }.first { it.item == findAfter }

        return goal.next.item
    }

    println(shortCircuit(steps = steps, loopCount = 2017, findAfter = 2017))

    // warning: STILL EXTREMELY SLOW
    println(shortCircuit(steps = steps, loopCount = 50_000_000, findAfter = 0))
}
