// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day04

import dev.codebasedlearning.adventofcode.commons.RCDir
import dev.codebasedlearning.adventofcode.commons.checkResult
import dev.codebasedlearning.adventofcode.commons.contentEquals
import dev.codebasedlearning.adventofcode.commons.linesOf
import dev.codebasedlearning.adventofcode.commons.print
import dev.codebasedlearning.adventofcode.commons.slice
import dev.codebasedlearning.adventofcode.commons.toGrid
import dev.codebasedlearning.adventofcode.commons.visit
import dev.codebasedlearning.adventofcode.commons.walk

val examples = listOf(
"""
MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
"""
)

fun main() {
    val story = object {
        val day = 4
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    // part 1: solutions: 18 / 2406

    val xmas = listOf('X','M','A','S')
    val xmasSize = xmas.size
    val grid = story.lines.toGrid()   // make it a 2D field

    checkResult(2406) { // [M3 28.228709ms]
        grid.positions.sumOf { pos ->
            RCDir.AllCardinals.count { dir -> pos.walk(dir).toGrid(grid).take(xmasSize).contentEquals(xmas) }
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (word search)") }

    // part 2: solutions: 9 / 1807

    val mas = listOf('M','S')
    val sam = listOf('S','M')
    val midElement = 'A'
    val extent = mas.size / 2
    checkResult(1807) { // [M3 10.471666ms]
        grid.positions.sumOf { pos ->
            // from pos consider only diagonal lines matching MAS in all variations
            if (grid[pos] != midElement) 0
            else {
                val uldr = (pos.visit(RCDir.UpLeft).slice(1..extent).toGrid(grid) +
                        pos.visit(RCDir.DownRight).slice(1..extent).toGrid(grid)).toList()
                val dlur = (pos.visit(RCDir.DownLeft).slice(1..extent).toGrid(grid) +
                        pos.visit(RCDir.UpRight).slice(1..extent).toGrid(grid)).toList()
                if ( (uldr==mas || uldr==sam) && (dlur==mas || dlur==sam) ) 1 else 0.toInt()
            }
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (xmas search)") }

}