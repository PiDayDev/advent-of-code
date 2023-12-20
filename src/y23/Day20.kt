package y23

import java.util.*

private const val DAY = "20"

enum class Pulse { LOW, HIGH }

data class Signal(val source: String, val destination: String, val pulse: Pulse)

sealed class Module(val id: String, val destinations: List<String>) {
    abstract fun receive(pulse: Pulse, from: String): List<Signal>
    fun transmit(pulse: Pulse) = destinations.map { dest -> Signal(id, dest, pulse) }
}

class FlipFlopModule(id: String, destinations: List<String>) : Module(id, destinations) {
    private var on = false

    override fun receive(pulse: Pulse, from: String): List<Signal> = when (pulse) {
        Pulse.LOW -> {
            on = !on
            transmit(if (on) Pulse.HIGH else Pulse.LOW)
        }

        else -> emptyList()
    }
}

class ConjunctionModule(id: String, destinations: List<String>) : Module(id, destinations) {
    val inputs: MutableList<String> = mutableListOf()
    private val ins = mutableMapOf<String, Pulse>()

    override fun receive(pulse: Pulse, from: String): List<Signal> {
        ins[from] = pulse
        return transmit(
            when {
                inputs.all { ins[it] == Pulse.HIGH } -> Pulse.LOW
                else -> Pulse.HIGH
            }
        )
    }
}

class BroadcastModule(destinations: List<String>) : Module("broadcast", destinations) {
    override fun receive(pulse: Pulse, from: String) = transmit(pulse)
}

fun String.toModule(): Module {
    val code = first()
    val id = drop(1).substringBefore(" ")
    val destinations = substringAfter(" -> ").split(", ")
    return when (code) {
        '%' -> FlipFlopModule(id, destinations)
        '&' -> ConjunctionModule(id, destinations)
        else -> BroadcastModule(destinations)
    }
}

fun main() {
    fun makeModuleMap(input: List<String>): Map<String, Module> {
        val modules: Map<String, Module> = input.map { it.toModule() }.associateBy { it.id }
        modules.values.forEach { m: Module ->
            m.destinations.forEach { d ->
                val destModule = modules[d]
                if (destModule is ConjunctionModule) {
                    destModule.inputs += m.id
                }
            }
        }
        return modules
    }

    fun part1(input: List<String>): Long {
        val modules = makeModuleMap(input)

        var countLo = 0
        var countHi = 0
        val broadcastModule = modules.values.filterIsInstance<BroadcastModule>().first()

        repeat(1000) {
            val queue = LinkedList<Signal>()
            queue.add(Signal("button", broadcastModule.id, Pulse.LOW))
            while (queue.isNotEmpty()) {
                val signal = queue.poll()

                if (signal.pulse == Pulse.LOW)
                    countLo++
                else
                    countHi++

                modules[signal.destination]
                    ?.receive(signal.pulse, signal.source)
                    ?.let { queue.addAll(it) }
            }
        }
        println("$countLo * $countHi")
        return countLo.toLong() * countHi.toLong()
    }

    fun part2(input: List<String>): Long {
        val modules = makeModuleMap(input)

        val broadcastModule = modules.values.filterIsInstance<BroadcastModule>().first()

        /**
        Hard-coding a bit of knowledge from my data:
            &gf -> rx
        so I have LOW to rx only when I have HIGH on all its inputs
            &qs -> gf
            &sv -> gf
            &pg -> gf
            &sp -> gf
        which means I must find the common period (lowest common multiple)
        for the four periods of HIGH signal on qs, sv, pg, sp
         */

        val periods = mutableMapOf<String,Int>()
        val indices = mutableMapOf<String,Int>()
        val goals = setOf("qs","sv","pg","sp")

        generateSequence(1) { it + 1 }
            .map { index ->
                val queue = LinkedList<Signal>()
                queue.add(Signal("button", broadcastModule.id, Pulse.LOW))
                while (queue.isNotEmpty()) {
                    val signal = queue.pollFirst()

                    val src = signal.source
                    if (src in goals && signal.pulse == Pulse.HIGH) {
                        when(src) {
                            in periods -> {}
                            in indices -> periods[src] = index - indices[src]!!
                            else -> indices[src] = index
                        }
                    }

                    val destination = signal.destination
                    val dest = modules[destination]
                    dest?.receive(signal.pulse, src)?.let { s -> queue.addAll(s) }
                }
            }
            .takeWhile { periods.size<goals.size }
            .last()

        return periods.values.fold(1L) {acc, period -> acc*period.toLong()}
    }


    val input = readInput("Day$DAY")
    println(part1(input))
    println(part2(input))
}
