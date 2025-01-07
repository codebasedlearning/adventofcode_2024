// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day08

import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.times
import dev.codebasedlearning.adventofcode.commons.geometry.plus
import dev.codebasedlearning.adventofcode.commons.geometry.minus
import dev.codebasedlearning.adventofcode.commons.geometry.unaryMinus

val examples = listOf(
"""
............
........0...
.....0......
.......0....
....0.......
......A.....
............
............
........A...
.........A..
............
............
"""
)

fun main() {
    val story = object {
        val day = 8
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val city = story.lines.toGrid()

    val antennas = city.positions.filter { city[it] != '.' }.groupBy { city[it] }

    fun createAntinodes(withHarmonics: Boolean) = antennas.values.flatMapTo(mutableSetOf<Position>()) { positions ->
        fun createNodes(start: Position, offset: Position): Sequence<Position> =
            generateSequence(1) { if (withHarmonics) it + 1 else null }
                .map { cnt -> start + offset * cnt }
                .takeWhile { it in city }

        positions.flatMapIndexed { i, pos1 ->
            positions.subList(i+1, positions.size).flatMap { pos2 ->
                (pos1-pos2).let { offset -> createNodes(pos1,offset) + createNodes(pos2,-offset) } }
        } + if (withHarmonics) positions else listOf()
    }

    // part 1: solutions: 14 / 291

    checkResult(291) { // [M3 11.608291ms]
        createAntinodes(withHarmonics = false).size
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (antinode locations)") }

    // part 2: solutions: 34 / 1015

    checkResult(1015) { // [M3 1.200833ms]
        createAntinodes(withHarmonics = true).size
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (with resonant harmonics)") }
}
