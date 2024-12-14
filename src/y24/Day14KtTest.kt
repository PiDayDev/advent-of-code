package y24

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class Day14KtTest {

    @Test
    fun `I can make a robot`() {
        val position = Position(0, 0)
        val velocity = Velocity(5, 6)
        val robot = Robot(position, velocity)

        assertEquals(position, robot.position)
        assertEquals(velocity, robot.velocity)
    }

    @Test
    fun `parsing a line returns a robot`() {
        val string = "p=6,3 v=-1,-3"
        val actualRobot = string.toRobot()

        val expectedRobot = Robot(
            Position(6, 3),
            Velocity(-1, -3)
        )

        assertEquals(expectedRobot, actualRobot)
    }

    @Test
    fun `robot moves`() {
        val robot = Robot(Position(6, 3), Velocity(-2, 5))

        val newRobot = robot.move()
        val expectedRobot = Robot(Position(4, 8), Velocity(-2, 5))

        assertEquals(expectedRobot, newRobot)
    }

    @Test
    fun `robot moves like pacman`() {
        val robot = Robot(Position(6, 3), Velocity(-7, 10))
        val grid = Grid(10, 10)

        val newRobot = robot.moveInside(grid)
        val expectedRobot = Robot(Position(9, 3), Velocity(-7, 10))

        assertEquals(expectedRobot, newRobot)
    }

    @Test
    fun `given sample file robots to move on position final`() {
        val grid = Grid(11, 7)
        val input = """
   p=0,4 v=3,-3
   p=6,3 v=-1,-3
   p=10,3 v=-1,2
   p=2,0 v=2,-1
   p=0,0 v=1,3
   p=3,0 v=-2,-2
   p=7,6 v=-1,-3
   p=3,0 v=-1,-2
   p=9,3 v=2,3
   p=7,3 v=-1,2
   p=2,4 v=2,-3
   p=9,5 v=-3,-3
  """.trimIndent().lines()

        val expectedPositions = listOf(
            Position(6, 0),
            Position(6, 0),
            Position(9, 0),
            Position(0, 2),
            Position(1, 3),
            Position(2, 3),
            Position(5, 4),
            Position(3, 5),
            Position(4, 5),
            Position(4, 5),
            Position(1, 6),
            Position(6, 6),
        )

        val result = parseAndMove100Times(input, grid)

        assertEquals(listOf<Position>(), expectedPositions - result)
        assertEquals(listOf<Position>(), result - expectedPositions)
    }

    @Test
    fun `count robots in a zone`() {
        val positions = listOf(
            Position(6, 0),
            Position(6, 0),
            Position(9, 0),
            Position(0, 2),
            Position(1, 3),
            Position(2, 3),
            Position(5, 4),
            Position(3, 5),
            Position(4, 5),
            Position(4, 5),
            Position(1, 6),
            Position(6, 6),
        )

        val quadrant = Quadrant(0..4, 0..2)
        val result = positions countIn quadrant

        assertEquals(1, result)
    }

    @Test
    fun `grid gives us quadrants`() {
        val grid = Grid(11, 7)

        val result = grid.quadrants()

        val expected = listOf<Quadrant>(
            Quadrant(0..4, 0..2),
            Quadrant(6..10, 0..2),
            Quadrant(0..4, 4..6),
            Quadrant(6..10, 4..6),
        )
        assertEquals(expected, result)
    }

    @Test
    fun `part1 fa giusto`() {
        val grid = Grid(11, 7)

        val input = """
           p=0,4 v=3,-3
           p=6,3 v=-1,-3
           p=10,3 v=-1,2
           p=2,0 v=2,-1
           p=0,0 v=1,3
           p=3,0 v=-2,-2
           p=7,6 v=-1,-3
           p=3,0 v=-1,-2
           p=9,3 v=2,3
           p=7,3 v=-1,2
           p=2,4 v=2,-3
           p=9,5 v=-3,-3
          """.trimIndent().lines()

        val result = safetyFactor(input, grid)

        assertEquals(12, result)
    }


}

