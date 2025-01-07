// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/14

package dev.codebasedlearning.adventofcode.day14

import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.grid.Grid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.plus

val examples = listOf(
// 1: ..
"""
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
"""
)

fun main() {
    val story = object {
        val day = 14
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
        val dims = if (example==0) Position(103,101) else Position(7,11) // from description
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val regex = """p[=](-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""".toRegex()
    fun Regex.findCoefficients(line:String) = this.find(line)!!.destructured.toList()

    data class Robot(val id: Int, var pos: Position, var velo: Position, val initPos: Position = pos) {
        fun reset() { pos = initPos }
    }

    // used in part 2 for visual output but also in part 1 for position arithmetics
    val room = Grid<String>(story.dims.row,story.dims.col) { "-" }

    var id = 0
    val robots = mutableSetOf<Robot>()
    for (line in story.lines) {
        val values = regex.findCoefficients(line).map { it.toInt() }
        robots.add(Robot(++id, pos = Position(values[1],values[0]), velo = Position(values[3],values[2])))
    }

    fun makeValidWithTurnAround(pos: Position, grid: Grid<*>)
            = if (grid.isValid(pos)) pos else {
        Position(
            (pos.row % grid.rows + grid.rows) % grid.rows,
            (pos.col % grid.cols + grid.cols) % grid.cols
        )
    }

    // part 1: solutions: 12 / 209409792

    checkResult(209409792) { // [M3 11.043084ms]
        repeat(100) {
            for (rob in robots) {
                rob.pos = makeValidWithTurnAround(rob.pos + rob.velo, room)
            }
        }

        val row2 = room.rows / 2
        val col2 = room.cols / 2
        val comparators = listOf<(Int, Int) -> Boolean>({ a, b -> a < b }, { a, b -> a > b })

        // count quadrants
        comparators.flatMap { rowComp -> comparators.map { colComp ->
            robots.count { rowComp(it.pos.row, row2) && colComp(it.pos.col, col2) }
        } }.fold(1) { acc, factor -> acc * factor }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (bathroom robots)") }

    // part 2: solutions: - / 8006

    val pattern = "#"
    fun longestPeriod(row: Int): Int {
        var maxLength = 0
        var currentLength = 0

        for (col in 0..<room.cols) {
            if (room[row,col] == pattern) {
                currentLength++
                if (currentLength > maxLength) maxLength = currentLength
            } else {
                currentLength = 0
            }
        }
        return maxLength
    }

    checkResult(8006) { // [M3 403.834792ms]
        for (rob in robots) rob.reset()

        val maxLoops = 10000    // guess
        val maxPattern = 20     // guess
        for (loop in 1..<maxLoops) {
            room.reset(story.dims.row,story.dims.col) { "-" }
            for (rob in robots) {
                rob.pos = makeValidWithTurnAround(rob.pos + rob.velo,room)
                room[rob.pos] = pattern
            }
            if ((0..<room.rows).any { row -> longestPeriod(row) > maxPattern }) {
                // show me the tree :-)
                // room.print(indent = 2, description = "loop $loop:", separator = "")
                return@checkResult loop
            }
        }
        -1
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (christmas tree)") }
}
