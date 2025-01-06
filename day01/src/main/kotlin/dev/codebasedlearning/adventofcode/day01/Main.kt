// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day01

import dev.codebasedlearning.adventofcode.commons.checkResult
import dev.codebasedlearning.adventofcode.commons.countWhile
import dev.codebasedlearning.adventofcode.commons.linesOf
import dev.codebasedlearning.adventofcode.commons.print
import kotlin.math.abs

val examples = listOf(
"""
3   4
4   3
2   5
1   3
3   9
3   3
"""
)

fun main() {
    val story = object {
        val day = 1
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val lineRegex = """(\d+)\s+(\d+)""".toRegex()
    // keep both lists at one place, no need for an extra class
    val sortedData = Pair(mutableListOf<Int>(), mutableListOf<Int>()).apply {
        for (line in story.lines) {
            lineRegex.matchEntire(line)!!.destructured.let { (n1, n2) ->
                first.add(n1.toInt())
                second.add(n2.toInt())
            }
        }
        first.sort()
        second.sort()
    }

    // Both parts are straightforward, so this is my shortest and most Kotlin-idiomatic solution with reasonable effort.
    // Three comments: a collection modelling a sorted list may be tempting, but it does not make sense (imho),
    // as all adds=inserts have to look for the right place to insert, but this is far more overhead than sorting the list
    // at the end once (in this situation here).
    // Second, technically the second part does not need to work on the sorted lists, but if you really want to,
    // you could simplify the counting by a) doing a binary search to find the first occurrence of a value
    // in the second list, and then b) simply counting down and up as long as the elements are equal
    // -> see example version 2.
    // Third, another idea utilizing maps with grouping and counting before. This is also a very fast solution
    // for large data sets.

    // part 1: solutions: 11 / 2057374

    checkResult(2057374) { // [M3 523us]
        sortedData.run { first.zip(second).sumOf { (n1, n2) -> abs(n1 - n2) } }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (total distance)") }

    // part 2: solutions: 31 / 23177084

    checkResult(23177084) { // [M3 6.750792ms]
        sortedData.run { first.sumOf { n1 -> n1 * second.count { n2 -> n1 == n2 } } }
    }.let { (dt,result,check) -> println("[part 2 v1] result: $result $check, dt: $dt (similarity score)") }

    // exploit the sorted structure...
    checkResult(23177084) { // [M3 810.125us]
        sortedData.run { first.sumOf { n1 -> n1 * second.run {
            binarySearch(n1).let { index ->
                if (index >= 0) 1 + countWhile(index - 1, -1, n1) + countWhile(index + 1, 1, n1) else 0
            }
        } } }
    }.let { (dt,result,check) -> println("[part 2 v2] result: $result $check, dt: $dt (alternative similarity score)") }

    // count before...
    checkResult(23177084) { // [M3 1.769416ms]
        sortedData.run {
            val countMap = second.groupingBy { it }.eachCount()
            first.sumOf { n1 -> n1 * (countMap[n1] ?: 0) }
        }
    }.let { (dt,result,check) -> println("[part 2 v3] result: $result $check, dt: $dt (alternative similarity score)") }
}
