// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/2

package dev.codebasedlearning.adventofcode.day02

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import kotlin.math.abs

val examples = listOf(
"""
7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
"""
)

fun main() {
    val story = object {
        val day = 2
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val reports = story.lines.map { line -> line.parseNumbers<Int>(' ') }

    /**
     * Checks if a list of integers is either strictly increasing or strictly decreasing
     * based on the provided direction, while ensuring the difference between
     * consecutive elements does not exceed a threshold (here 3 is fixes).
     */
    fun checkReportOneDirection(line: List<Int>, incOrDec: Boolean)
            = line.zipWithNext().all { (prev, current) ->
        (incOrDec && prev < current || !incOrDec && prev > current) && abs(current - prev) <= 3
    }

    /** checks in both directions */
    fun checkReport(line: List<Int>)
            = checkReportOneDirection(line,incOrDec = true) || checkReportOneDirection(line,incOrDec = false)

    /** checks with one possible exception */
    fun checkTolerantReport(line: List<Int>)
            = checkReport(line)
            || line.indices.any { i -> checkReport(line.toMutableList().apply { removeAt(i) }) }
    // here, technically cloning can be avoided if MutableList are used from the beginning,
    // but due to the insert and remove operations there is no benefit

    // part 1: solutions: 2 / 299

    checkResult(299) { // [M3 1.321416ms]
        reports.count { line -> checkReport(line) }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (safe reports)") }

    // part 2: solutions: 4 / 364

    checkResult(364) { // [M3 4.178792ms]
        reports.count { line -> checkTolerantReport(line) }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (tolerant safe reports)") }
}
