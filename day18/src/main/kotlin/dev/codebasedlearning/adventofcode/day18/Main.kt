// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day18

import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.graph.minimalSteps
import dev.codebasedlearning.adventofcode.commons.graph.toGraph
import dev.codebasedlearning.adventofcode.commons.grid.Grid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
// 1: ..
"""
5,4
4,2
4,5
3,0
2,1
6,3
2,4
1,5
0,6
3,3
2,6
5,1
1,2
5,5
2,5
6,5
1,4
0,4
6,4
1,1
6,1
1,0
0,5
1,6
2,0
"""
)

fun main() {
    val story = object {
        val day = 18
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
        val dims = if (example==0) Position(71,71) else Position(7,7)
        val down = if (example==0) 1024 else 12
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val falling = story.lines.map { it.split(',').map { it.toInt() } }

    fun minimalSteps(simDown: Int): Int
            = Grid<Char>(rows = story.dims.row, cols = story.dims.col) { pos -> '.' }.run {
        falling.take(simDown).forEach { (x,y) -> this[y,x] = '#' }
        toGraph().minimalSteps(Position(0,0), Position(story.dims.row-1,story.dims.col-1))
    }

    // part 1: solutions: 22 / 226

    checkResult(226) { // [M3 37.279542ms]
        minimalSteps(story.down)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (minimum number of steps)") }

    // part 2: solutions: 6,1 / 60,46

    checkResult("60,46") { // [M3 28.305125ms]
        var (left,right) = story.down+1 to falling.size-1     // bin search
        var res = right
        while (left <= right) {
            val mid = (right + left) / 2
            val steps = minimalSteps(mid)
            if (steps==-1) { res=mid; right = mid-1 } else { left = mid+1 }
        }
        falling[res-1].let { (x,y) -> "${x},${y}" }
    }.let { (dt,result,check) -> println("[part 2] result: '$result' $check, dt: $dt (coordinates of the first byte)") }
}
