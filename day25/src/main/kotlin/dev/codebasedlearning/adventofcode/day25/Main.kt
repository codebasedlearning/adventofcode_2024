// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day25

import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
// 1: ..
"""
#####
.####
.####
.####
.#.#.
.#...
.....

#####
##.##
.#.##
...##
...#.
...#.
.....

.....
#....
#....
#...#
#.#.#
#.###
#####

.....
.....
#.#..
###..
###.#
###.#
#####

.....
.....
.....
#....
#.#..
#.#.#
#####
"""
)

fun main() {
    val story = object {
        val day = 25
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val blocks = story.lines.toBlocks()

    val lockIndicator = blocks[0][0]
    val pins = lockIndicator.length
    val spaces = blocks[0].size - 1

    val (locks,keys) = blocks.map { it.toGrid() }.mapIndexed { i, f ->
        val occupied = (0..<pins).map { pin -> (0..spaces).count { f[it,pin] == '#' } - 1 }
        if (blocks[i][0]==lockIndicator) occupied to null else null to occupied
    }.unzip().let { (x,y) -> x.filterNotNull() to y.filterNotNull() } // instead if lock.add / keys.add... better?

    // part 1: solutions: 3 / 3201

    checkResult(3201) { // [M3 14.417125ms]
        locks.sumOf { lock ->
            keys.count { key ->
                lock.zip(key).all { (l, k) -> l + k < spaces }
            }
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (locks and keys)") }

    // part 2: solutions: . / . chronicles delivered
}
