// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/21

package dev.codebasedlearning.adventofcode.day21

import dev.codebasedlearning.adventofcode.commons.graph.findAllShortestPaths
import dev.codebasedlearning.adventofcode.commons.graph.toGraph
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.minus
import dev.codebasedlearning.adventofcode.commons.grid.mapDirToKeys

val examples = listOf(
// 1: ..
"""
029A
980A
179A
456A
379A
"""
)

fun main() {
    val story = object {
        val day = 21
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val codes = story.lines.map { it to it.substringBeforeLast('A').toLong() }

    val numPad = object {
        val layout = """
            789
            456
            123
            #0A
        """.trimIndent()
        val lines = linesOf(input = layout)
        val chars = lines.joinToString("").replace("#", "")
        val grid = lines.toGrid()
        val pos = grid.positions.filter { grid[it] in chars }.associateBy { grid[it] }
        val graph = grid.toGraph(chars)
        val paths = chars.associateWith { c1 ->
            chars.associateWith { c2 ->
                graph.findAllShortestPaths(pos[c1]!!, pos[c2]!!).map { list ->
                    list.zipWithNext { a, b -> mapDirToKeys[(b - a).asDir]!! }.joinToString("")
                }
            }
        }
    }

    val dirPad = object {
        val layout = """
            #^A
            <v>
        """.trimIndent()
        val lines = linesOf(input = layout)
        val chars = lines.joinToString("").replace("#", "")
        val grid = lines.toGrid()
        val pos = grid.positions.filter { grid[it] in chars }.associateBy { grid[it] }
        val graph = grid.toGraph(chars)
        val paths = chars.associateWith { c1 ->
            chars.associateWith { c2 ->
                graph.findAllShortestPaths(pos[c1]!!, pos[c2]!!).map { list ->
                    list.zipWithNext { a, b -> mapDirToKeys[(b - a).asDir]!! }.joinToString("")
                }
            }
        }
    }

    val allCodeLengths: MutableMap<Pair<String, Int>, Long> = mutableMapOf()

    fun calcLength(code: String, level: Int, pad: Map<Char, Map<Char, List<String>>>): Long
            = allCodeLengths.getOrPut(code to level) {
        if (level == 0) code.length.toLong() else
            "A$code".zipWithNext().sumOf { (start, end) ->
                pad[start]!![end]!!.minOf { path -> calcLength("${path}A", level - 1, dirPad.paths) }
            }
    }

    // part 1: solutions: 126384 / 152942

    checkResult(152942) { // [M3 529.209us]
        codes.sumOf { (code, num) -> calcLength(code, 2 + 1, numPad.paths) * num }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (starship panels)") }

    // part 2: solutions: . / 189235298434780

    checkResult(189235298434780) { // [M3 1.351166ms]
        codes.sumOf { (code, num) -> calcLength(code, 25 + 1, numPad.paths) * num }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (more starship panels)") }

}
