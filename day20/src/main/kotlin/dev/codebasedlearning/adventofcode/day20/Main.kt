// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day20

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.norm1
import dev.codebasedlearning.adventofcode.commons.geometry.toDirectionSquare
import dev.codebasedlearning.adventofcode.commons.geometry.walk
import dev.codebasedlearning.adventofcode.commons.graph.findShortestPaths
import dev.codebasedlearning.adventofcode.commons.graph.minimalSteps
import dev.codebasedlearning.adventofcode.commons.graph.toGraph
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
// 1: ..
"""
###############
#...#...#.....#
#.#.#.#.#.###.#
#S#...#.#.#...#
#######.#.#.###
#######.#.#...#
#######.#.###.#
###..E#...#...#
###.#######.###
#...###...#...#
#.#####.#.###.#
#.#...#.#.#...#
#.#.#.#.#.#.###
#...#...#...###
###############
"""
)

fun main() {
    val story = object {
        val day = 20
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val grid = story.lines.toGrid()
    val startPos = grid.positions.find { grid[it] == 'S' }!!.also { grid[it] = '.' }
    val endPos = grid.positions.find { grid[it] == 'E' }!!.also { grid[it] = '.' }

    val graph = grid.toGraph()
    val startPaths = graph.findShortestPaths(startPos)  // reachable from start
    val endPaths = graph.findShortestPaths(endPos)      // end reachable from here
    val fairDist = graph.minimalSteps(startPos,endPos)  // Benchmark if played fair

    fun cheats(cutOff: Int, worthIt: Int): Int {
        val total = mutableMapOf<Int,Int>()
        grid.positions.filter { it in startPaths.distances }.forEach { pos ->
            pos.walk(cutOff.toDirectionSquare()) // Direction.Square(cutOff)
                .filter { it.dir.norm1 <= cutOff && it.pos in endPaths.distances } // implicitly: it.pos in grid
                .forEach { (cheatPos,dir) ->
                    val cheated = fairDist - (startPaths.distances[pos]!!.toInt() + dir.norm1 + endPaths.distances[cheatPos]!!.toInt())
                    if (cheated >= worthIt) {
                        total[cheated] = total.getOrDefault(cheated, 0).toInt() + 1
                    }
                }
        }
        return total.values.sumOf { it }
    }

    // part 1: solutions: 44 / 1346

    checkResult(1346) { // [M3 40.250916ms]
        cheats(2,if (story.example==0) 100 else 0)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: 285 / 985482

    checkResult(985482) { // [M3 784.168ms]
        cheats(20,if (story.example==0) 100 else 50)
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
