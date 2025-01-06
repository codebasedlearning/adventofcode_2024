// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day99

import dev.codebasedlearning.adventofcode.commons.checkResult
import dev.codebasedlearning.adventofcode.commons.linesOf
import dev.codebasedlearning.adventofcode.commons.print

val examples = listOf(
"""
abc
"""
)

fun main() {
    val story = object {
        val day = 99
        val year = 2024
        val example = 1
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }


    // part 1: solutions: 11 / 2057374

    checkResult(1) { // [M3 523us]
        1
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (total distance)") }

    // part 2: solutions: 31 / 23177084

    checkResult(2) { // [M3 6.750792ms]
        2
    }.let { (dt,result,check) -> println("[part 2 v1] result: $result $check, dt: $dt (similarity score)") }

}
