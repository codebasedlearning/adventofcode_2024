// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day06

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.geometry.visit
import dev.codebasedlearning.adventofcode.commons.grid.Grid
import dev.codebasedlearning.adventofcode.commons.grid.mapDirToKeys
import dev.codebasedlearning.adventofcode.commons.grid.takeWhileInGrid
import dev.codebasedlearning.adventofcode.commons.grid.temporarilyReplace
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.times
import dev.codebasedlearning.adventofcode.commons.geometry.plus

val examples = listOf(
    """        
....#.....
.........#
..........
..#.......
.......#..
..........
.#..^.....
........#.
#.........
......#...
"""
)

fun main() {
    val story = object {
        val day = 6
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val theLab = story.lines.toGrid()
    val (guard,facing) = theLab.positions.first { pos -> theLab[pos] !in ".#" }.let { it to mapDirToKeys[theLab[it]]!! }

    // remember the way but also check for spaces been before, but only in the same direction, so crossing is allowed
    fun MutableMap<Position,Direction>.fillAndCheckWay(positions: Iterable<Position>, dir:Direction): Boolean {
        positions.forEach { pos ->
            if (this[pos] == dir) return true
            this[pos] = dir
        }
        return false
    }

    // patrol the lab (all these seek and check feels a little quirky...)
    fun Grid<Char>.patrol(): Pair<MutableMap<Position,Direction>, Boolean> {
        val visited = mutableMapOf<Position,Direction>(guard to facing)

        var current = guard
        var direction = facing
        var stuck = false
        while (true) {
            //var way1 = this.linePositions(start = current, direction = direction, skipStart = true).toList()
            var way = current.visit(direction).drop(1).takeWhileInGrid(this).toList()
            var (spaces, blocked) = way.indexOfFirst { pos -> this[pos]=='#' }
                .let { index -> if (index>=0) Pair(index,true) else Pair(way.size,false)}
            stuck = visited.fillAndCheckWay(way.take(spaces), direction)

            if (!blocked || stuck) break;

            current += direction * spaces
            direction = direction.clockWise()
        }
        return Pair(visited, stuck)
    }

    var guardsWay = mutableMapOf<Position,Direction>()

    // part 1: solutions: 41 / 4883

    checkResult(4883) { // [M3 5.973625ms]
        theLab.patrol().let { (visited, _) -> guardsWay.putAll(visited); visited.size }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (distinct positions)") }

    guardsWay.remove(guard) // skip starting postion

    // part 2: solutions: 6 / 1655

    checkResult(1655) { // [M3 838.868625ms]
        guardsWay.keys.count { pos ->
            theLab.temporarilyReplace(pos,'#') { patrol() }.let { (_, stuck) -> stuck }
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (obstacles)") }
}
