// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/10

package dev.codebasedlearning.adventofcode.day10

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.plus

val examples = listOf(
// 1: trailheads 2
"""
...0...
...1...
...2...
6543456
7.....7
8.....8
9.....9
""",
// 2: trailheads 4
"""
..90..9
...1.98
...2..7
6543456
765.987
876....
987....
""",
// 3: trailheads 2
"""
10..9..
2...8..
3...7..
4567654
...8..3
...9..2
.....01
""",
// 4: trailheads 36, rating 81
"""
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
""",
// 5: rating 81
"""
.....0.
..4321.
..5..2.
..6543.
..7..4.
..8765.
..9....
""",
// 6: rating 13
"""
..90..9
...1.98
...2..7
6543456
765.987
876....
987....
""",
// 7: rating 227
"""
012345
123456
234567
345678
4.6789
56789.
""",
// 8: rating 81
"""
89010123
78121874
87430965
96549874
45678903
32019012
01329801
10456732
"""
)

fun main() {
    val story = object {
        val day = 10
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val guide = story.lines.toGrid { c -> c.digitToIntOrNull() ?: -1 }

    fun nextCrossWayPositions(start: Position, past: Position? = null)
            = sequence { yieldAll(Direction.Cardinals.map { start + it }) }
        .filter { guide.isValid(it) && (past==null || it != past) }

    fun followTrail(start: Position, expected: Int, highest: MutableMap<Position,Int>, past:Position? = null) {
        guide[start].let { c -> when {
            c==9 && expected==9 -> highest[start] = highest.getOrDefault(start, 0) + 1
            c==expected /* && c!=-1 */ -> nextCrossWayPositions(start,past).forEach {
                followTrail(it,expected+1,highest,start)
            }
        } }
    }

    // part 1: solutions: 550
    // part 2: solutions: 1255

    checkResult(550 to 1255) { // [M3 9.740667ms]
        guide.positions
            .filter { guide[it] == 0 }
            .fold(0 to 0) { acc, start ->
                mutableMapOf<Position,Int>().let { highest ->
                    followTrail(start,0,highest)
                    acc.first + highest.size to acc.second + highest.values.sum()
                }
            }
    }.let { (dt,result,check) -> println("[part 1|2] results: ${result.first}|${result.second} $check, dt: $dt (hiking)") }
}
