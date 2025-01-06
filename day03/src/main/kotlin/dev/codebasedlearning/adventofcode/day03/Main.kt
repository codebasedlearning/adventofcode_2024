// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day03

import dev.codebasedlearning.adventofcode.commons.checkResult
import dev.codebasedlearning.adventofcode.commons.linesOf
import dev.codebasedlearning.adventofcode.commons.parseNumbers
import dev.codebasedlearning.adventofcode.commons.print

val examples = listOf(
"""
xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))
""",
"""
xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))
"""
)

fun main() {
    val story = object {
        val day = 3
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    fun String.extractProduct() = this.parseNumbers<Int>(',').let { (n1,n2) -> n1 * n2 }

    // part 1: solutions: 161 / 166630675

    checkResult(166630675) { // [M3 7.466041ms]
        val regex = Regex("mul\\((\\d+,\\d+)\\)")
        story.lines.sumOf { line ->
            regex.findAll(line).sumOf { it.groupValues[1].extractProduct() }
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (corrupted memory)") }

    // part 2: solutions: 48 / 93465710

    checkResult(93465710) { // [M3 1.264500ms]
        var switch = true
        val regex = Regex("mul\\((\\d+,\\d+)\\)|do\\(\\)|don't\\(\\)")
        story.lines.sumOf { line ->
            regex.findAll(line).sumOf { when (it.value) {
                "do()" -> { switch = true; 0 }
                "don't()" -> { switch = false; 0 }
                else -> if (switch) it.groupValues[1].extractProduct() else 0
            } }
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (corrupted memory switched)") }
}
