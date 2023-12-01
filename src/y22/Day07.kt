package y22

private const val DAY = "07"

private sealed class Item {
    var parent: Directory? = null

    abstract fun totalSize(): Long

    fun root(): Directory = generateSequence(currentDirectory()) { it.parent }.last()

    private fun currentDirectory() = when (this) {
        is Directory -> this
        else -> parent!!
    }

}

private class Directory : Item() {
    val files = mutableMapOf<String, File>()
    val directories = mutableMapOf<String, Directory>()

    fun makeDir(relativePath: String) = directories
        .getOrPut(relativePath) {
            Directory().also { it.parent = this }
        }

    fun touchFile(relativePath: String, size: Long) = files
        .getOrPut(relativePath) {
            File(size).also { it.parent = this }
        }

    override fun totalSize() =
        (files.values + directories.values).sumOf { it.totalSize() }

    fun allDirectories(): List<Directory> =
        listOf(this) + directories.values.flatMap { it.allDirectories() }

    fun cd(relativePath: String) = when (relativePath) {
        "/" -> root()
        ".." -> parent!!
        else -> makeDir(relativePath)
    }
}

private class File(val size: Long) : Item() {
    override fun totalSize() = size
}

private const val CD = "$ cd "
private const val LS = "$ ls"
private const val DIR = "dir "

private fun List<String>.toFileSystem(): Directory {
    val root = Directory()
    fold(root) { folder, line -> folder.process(line) }
    return root
}

private fun Directory.process(line: String): Directory {
    if (line.startsWith(LS)) return this

    if (line.startsWith(CD)) return cd(line.substringAfter(CD))

    if (line.startsWith(DIR)) {
        makeDir(line.substringAfter(DIR))
    } else {
        val (size, name) = line.split(" ")
        touchFile(name, size.toLong())
    }
    return this
}


private const val TOTAL_DISK_SPACE = 70000000
private const val FREE_DISK_SPACE_GOAL = 30000000

fun main() {

    fun part1(directorySizes: List<Long>) =
        directorySizes
            .filter { it <= 100000L }
            .sumOf { it }

    fun part2(directorySizes: List<Long>, neededDiskSpace: Long) =
        directorySizes
            .filter { it >= neededDiskSpace }
            .minOf { it }

    val input = readInput("Day$DAY")

    val fileSystem = input.toFileSystem()
    val directorySizes = fileSystem.allDirectories().map { it.totalSize() }
    val currentFreeDiskSpace = TOTAL_DISK_SPACE - fileSystem.totalSize()
    val neededDiskSpace = FREE_DISK_SPACE_GOAL - currentFreeDiskSpace

    println(part1(directorySizes))
    println(part2(directorySizes, neededDiskSpace))
}
