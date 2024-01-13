import re

file1 = open('../Day02.txt', 'r')
Lines = file1.readlines()


class Bag:
    def __init__(self, red, green, blue):
        self.r = red
        self.g = green
        self.b = blue

    def can_be_inside(self, other):
        return self.r <= other.r and self.g <= other.g and self.b <= other.b

    def power(self):
        return self.r * self.g * self.b

    def compose(self, other):
        return Bag(max(self.r, other.r), max(self.g, other.g), max(self.b, other.b))


limit = Bag(12, 13, 14)
total_part1 = 0
total_part2 = 0
for line in Lines:
    parts = re.split("[:;\n] *", line)
    game = parts[0]
    gameId = int(game.split(" ")[1])
    rest = parts[1::]
    rest.pop()
    possible = 1
    min_bag = Bag(0, 0, 0)
    for extraction in rest:
        cubes = re.split(", ", extraction)
        r = 0
        b = 0
        g = 0
        for cube in cubes:
            (count, color) = cube.split(" ")
            match color:
                case "red":
                    r = int(count)
                case "green":
                    g = int(count)
                case "blue":
                    b = int(count)
        current = Bag(r, g, b)
        min_bag = min_bag.compose(current)
        if not current.can_be_inside(limit):
            possible = 0

    total_part1 += possible * gameId
    total_part2 += min_bag.power()

print("PART 1 = " + str(total_part1))
print("PART 2 = " + str(total_part2))
