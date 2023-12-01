package y22

import kotlin.math.max

private const val DAY = 19

private data class Materials(
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0
) {
    companion object {
        val ORE = Materials(ore = 1)
        val CLAY = Materials(clay = 1)
        val OBSIDIAN = Materials(obsidian = 1)
        val GEODE = Materials(geode = 1)
    }

    private operator fun Int.times(s: String) =
        if (this == 0) "" else "$this $s"

    override fun toString() =
        listOf(ore * "ore", clay * "clay", obsidian * "obsidian", geode * "geode")
            .filter { it.isNotBlank() }
            .joinToString(", ")

    operator fun plus(materials: Materials) = Materials(
        ore + materials.ore, clay + materials.clay, obsidian + materials.obsidian, geode + materials.geode
    )

    operator fun minus(materials: Materials) = Materials(
        ore - materials.ore, clay - materials.clay, obsidian - materials.obsidian, geode - materials.geode
    )

    operator fun times(n: Int) = Materials(
        ore * n, clay * n, obsidian * n, geode * n
    )

    /** How many times you need materials to make this. Null if impossible */
    operator fun div(materials: Materials): Int? {
        if (materials.geode == 0 && geode > 0) return null
        if (materials.obsidian == 0 && obsidian > 0) return null
        if (materials.clay == 0 && clay > 0) return null
        if (materials.ore == 0 && ore > 0) return null
        return generateSequence(0) { it + 1 }
            .firstOrNull { (materials * it) gte this }
    }

    infix fun gte(other: Materials): Boolean =
        listOf(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)
            .none { it < 0 }

}

private typealias Bots = Materials

private fun Bots.howLongToBuild(robot: Robot, stock: Materials): Int? = (robot.cost - stock) / this

private data class Robot(val cost: Materials, val output: Materials) {
    override fun toString() = "Robot[$cost] => $output"
}

private data class Blueprint(
    val id: Int,
    val oreBot: Robot,
    val clayBot: Robot,
    val obsidianBot: Robot,
    val geodeBot: Robot
) {
    override fun toString() = """
    Blueprint # $id
     - oreBot       = $oreBot
     - clayBot      = $clayBot
     - obsidianBot  = $obsidianBot
     - geodeBot     = $geodeBot""".trimIndent()
}

private fun String.toBlueprint(): Blueprint {
    val id = substringAfter("Blueprint ").substringBefore(":").toInt()
    val oreForOre = substringAfter("ore robot costs ").substringBefore(" ore").toInt()
    val oreBot = Robot(
        cost = Materials(ore = oreForOre),
        output = Materials.ORE
    )
    val oreForClay = substringAfter("clay robot costs ").substringBefore(" ore").toInt()
    val clayBot = Robot(
        cost = Materials(ore = oreForClay),
        output = Materials.CLAY
    )
    val obsidian = substringAfter("obsidian robot costs ")
    val oreForObsidian = obsidian.substringBefore(" ore").toInt()
    val clayForObsidian = obsidian.substringAfter("and ").substringBefore(" clay").toInt()
    val obsidianBot = Robot(
        cost = Materials(ore = oreForObsidian, clay = clayForObsidian),
        output = Materials.OBSIDIAN
    )
    val geode = substringAfter("geode robot costs ")
    val oreForGeode = geode.substringBefore(" ore").toInt()
    val obsidianForGeode = geode.substringAfter("and ").substringBefore(" obsidian").toInt()
    val geodeBot = Robot(
        cost = Materials(ore = oreForGeode, obsidian = obsidianForGeode),
        output = Materials.GEODE
    )
    return Blueprint(id, oreBot, clayBot, obsidianBot, geodeBot)
}

fun main() {

    fun optimize(blueprint: Blueprint, residualTime: Int, availableMaterials: Materials, availableBots: Bots, currentBest: Int): Int {
        val (_, ore, clay, obsidian, geode) = blueprint

        if (residualTime <= 0) return currentBest

        val projection = availableMaterials.geode + availableBots.geode * residualTime
        val maxFinalGeode = projection + residualTime * (residualTime - 1) / 2
        var best = max(currentBest, projection)

        if (maxFinalGeode < currentBest) return best

        val botToTime: Map<Robot, Int> = listOf(ore, clay, obsidian, geode)
            .reversed()
            .associateWith { availableBots.howLongToBuild(it, availableMaterials) }
            .filterValues { it != null }
            .mapValues { (_, v) -> v as Int }

        for ((bot, time) in botToTime) {
            val t = residualTime - time - 1
            val mats = availableBots * (time + 1) + availableMaterials - bot.cost
            val bots = availableBots + bot.output
            if (t >= 0)
                best = optimize(blueprint, t, mats, bots, best)
        }

        return best
    }

    fun optimize(blueprint: Blueprint, maxTime: Int) =
        optimize(blueprint, maxTime, availableMaterials = Materials(), availableBots = Bots(ore = 1), currentBest = 0)

    val input = readInput("Day$DAY")
    val blueprints = input.map { it.toBlueprint() }

    fun part1(blueprints: List<Blueprint>) = blueprints
        .mapIndexed { index, blueprint ->
            val value = optimize(blueprint, 24)
            val j = index + 1
            println("$j => $value")
            value * j
        }
        .sum()

    fun part2(blueprints: List<Blueprint>) = blueprints
        .take(3)
        .map { blueprint ->
            val value = optimize(blueprint, 32)
            println("==> $value")
            value
        }
        .fold(1) { a, b -> a * b }

    println(part1(blueprints))
    println(part2(blueprints))

}

