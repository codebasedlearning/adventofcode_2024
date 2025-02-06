// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/15

package dev.codebasedlearning.adventofcode.day15

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.grid.Grid
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.plus
import dev.codebasedlearning.adventofcode.commons.grid.mapKeysToDir

val examples = listOf(
// 1: ..
"""
########
#..O.O.#
##@.O..#
#...O..#
#.#.O..#
#...O..#
#......#
########

<^^>>>vv<v>>v<<
""",
"""
##########
#..O..O.O#
#......O.#
#.OO..O.O#
#..O@..O.#
#O#..O...#
#O..O..O.#
#.OO.O.OO#
#....O...#
##########

<vv>^<v^>v>^vv^v>v<>v^v<v<^vv<<<^><<><>>v<vvv<>^v^>^<<<><<v<<<v^vv^v>^
vvv<<^>^v^^><<>>><>^<<><^vv^^<>vvv<>><^^v>^>vv<>v<<<<v<^v>^<^^>>>^<v<v
><>vv>v^v^<>><>>>><^^>vv>v<^^^>>v^v^<^^>v^^>v^<^v>v<>>v^v^<v>v^^<^^vv<
<<v<^>>^^^^>>>v^<>vvv^><v<<<>^^^vv^<vvv>^>v<^^^^v<>^>vvvv><>>v^<<^^^^^
^><^><>>><>^^<<^^v>>><^<v>^<vv>>v>>>^v><>^v><<<<v>>v<v<v>vvv>^<><<>^><
^>><>^v<><^vvv<^^<><v<<<<<><^v<<<><<<^^<v<^^^><^>>^<v^><<<^>>^v<v^v<v^
>^>>^v>vv>^<<^v<>><<><<v<<v><>v<^vv<<<>^^v^>^^>>><<^v>>v^v><^^>>^<>vv^
<><^^>^^^<><vvvvv^v<v<<>^v<v>v<<^><<><<><<<^^<<<^<<>><<><^^^>^^<>^>v<>
^^>vv<^v^v<vv>^<><v<^v>^^^>>>^^vvv^>vvv<>>>^<^>>>>>^<<^v>^vvv<>^<><<v>
v^^>>><<^^<>>^v^<v^vv<>v^<<>^<^v^v><^<<<><<^<v><v<>vv>>v><v^<vv<>v^<<^
""",
"""
#######
#...#.#
#.....#
#..OO@#
#..O..#
#.....#
#######

<vv<<^^<<^^
"""
)

fun main() {
    val story = object {
        val day = 15
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val (warehouseLoaded, moves) = story.lines.toBlocks().let { it[0].toGrid() to it[1].joinToString(separator = "") }
    // warehouseLoaded.print(indent = 2, description = "loaded warehouse:", separator = "")
    // println("moves: $moves\n")

    fun <T> Grid<T>.copy():Grid<T> {
        val f = Grid<T>()
        for (l in data) {
            f.data.add(l.toMutableList())
        }
        return f
    }

    fun sumGpsPositions(isExtended: Boolean): Long {
        val warehouseStart = if (!isExtended) warehouseLoaded.copy() else Grid<Char>().apply {
            val repl = mapOf('#' to "##", '.' to "..", 'O' to "[]", '@' to "@.")
            this.addAll((0..<warehouseLoaded.rows).map { row ->
                (0..<warehouseLoaded.cols).flatMap { col ->
                    repl[warehouseLoaded[row, col]].let { mutableListOf(it!![0], it[1]) }
                }.toMutableList()
            })
        }
        var startPos = warehouseStart.positions.find { warehouseStart[it] == '@' }!!
        warehouseStart[startPos] = '.'

        var ware = warehouseStart.copy()
        var pos = startPos

        fun affectedSimpleBoxes(from: Position, dir: Direction) =
            mutableListOf<MutableSet<Position>>().apply {
                var pos = from
                while (ware[pos] == 'O') {
                    add(mutableSetOf(pos)); pos += dir
                }
                if (ware[pos] == '#') {
                    clear()
                }
            }

        fun affectedHorizontalDoubleBoxes(from: Position, dir: Direction) =
            mutableListOf<MutableSet<Position>>().apply {
                var pos = from
                while (ware[pos] == '[' || ware[pos] == ']') {  // @[] or []@
                    add(mutableSetOf(pos)); pos += dir
                    add(mutableSetOf(pos)); pos += dir
                }
                if (ware[pos] == '#') {
                    clear()
                }
            }

        fun affectedVerticalDoubleBoxes(from: Position, dir: Direction) =
            mutableListOf<MutableSet<Position>>().apply {
                var pos = from
                var c = ware[pos]
                // starter
                if (c == ']') {
                    add(mutableSetOf(pos + Direction.Left, pos))
                } else {
                    add(mutableSetOf(pos, pos + Direction.Right))
                }

                // collect affected boxes
                while (true) {
                    val lastBoxes = this.last()
                    val newBoxes = mutableSetOf<Position>()
                    for (box in lastBoxes) {
                        val nextPos = box + dir
                        val nc = ware[nextPos]
                        if (nc == '[' || nc == ']') {
                            newBoxes.add(nextPos)
                            newBoxes.add(box + dir + (if (nc == '[') Direction.Right else Direction.Left))
                        }
                    }
                    if (newBoxes.isEmpty()) break
                    else this.add(newBoxes)
                }
            }

        moves.forEach { move ->
            val dir = mapKeysToDir[move]!!
            val c = ware[pos + dir]
            when (c) {
                '#' -> {}
                '.' -> pos += dir
                'O', '[', ']' -> {
                    var affected = when {
                        c == 'O' -> affectedSimpleBoxes(pos + dir, dir)
                        dir.isHorizontal -> affectedHorizontalDoubleBoxes(pos + dir, dir)
                        dir.isVertical -> affectedVerticalDoubleBoxes(pos + dir, dir)
                        else -> mutableListOf()
                    }

                    if (affected.isNotEmpty()) {
                        var fieldCopy = ware.copy()

                        // copy fields if free
                        var allFree = true
                        while (affected.isNotEmpty() && allFree) {
                            val last = affected.removeLast()
                            if (!last.all { ware[it + dir] == '.' })
                                allFree = false
                            else
                                last.forEach { ware[it + dir] = ware[it]; ware[it] = '.' }
                        }
                        if (!allFree)
                            ware = fieldCopy
                        else
                            pos += dir
                    }
                }
            }
        }

        return ware.positions.sumOf { pos ->
            if (ware[pos] == 'O' || ware[pos] == '[') pos.row * 100L + pos.col * 1L else 0L
        }
    }

    // part 1: solutions: 2028 / 1415498

    checkResult(1415498) { // [M3 20.646875ms]
        sumGpsPositions(isExtended = false)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (boxes)") }

    // part 2: solutions: 1751 / 1432898

    checkResult(1432898) { // [M3 18.621625ms]
        sumGpsPositions(isExtended = true)
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (larger boxes)") }
}
