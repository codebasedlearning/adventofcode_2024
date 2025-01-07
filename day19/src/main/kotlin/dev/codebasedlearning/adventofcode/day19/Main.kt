// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day19

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
// 1: ..
    """
r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb
"""
)

fun main() {
    val story = object {
        val day = 19
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
    val towels = blocks[0][0].split(",").map { it.trim() }.toSet()
    val stripes = blocks[1].map { it.trim() }

    println("towels: $towels")
    stripes.print(indent = 2, description = "stripes:", take = 2)

    fun countArrangements(stripe: String, memoization: MutableMap<String,Long> = mutableMapOf()): Long {
        if (stripe.isEmpty()) return 1L
        memoization[stripe]?.let { return it }

        return towels.sumOf {
            if (stripe.startsWith(it)) countArrangements(stripe.substring(it.length),memoization) else 0L
        }.apply { memoization[stripe] = this }
    }

    // part 1: solutions: 6 / 283

    checkResult(283) { // [M3 83.581917ms]
        stripes.count { countArrangements(it) > 0 }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (designs)") }

    // part 2: solutions: 16 / 615388132411142

    checkResult(615388132411142) { // [M3 94.447ms]
        stripes.sumOf { countArrangements(it) }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (arrangements)") }
}
