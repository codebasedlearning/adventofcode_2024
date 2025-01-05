// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day01

import dev.codebasedlearning.adventofcode.commons.checkResult
import dev.codebasedlearning.adventofcode.commons.fetchAoCInputIfNeeded
import dev.codebasedlearning.adventofcode.commons.countWhile
import dev.codebasedlearning.adventofcode.commons.inBrightYellow
import dev.codebasedlearning.adventofcode.commons.linesOf
import dev.codebasedlearning.adventofcode.commons.print
import dev.codebasedlearning.adventofcode.commons.timeResult
import kotlin.math.abs

const val day = 1
fun main() {
//    val name = "Kotlin!"
//    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//    // to see how IntelliJ IDEA suggests fixing it.
//    println("Hello, " + name + "!".inBold())
//
//    checkResult(2057374) { // [M3 523us]
//        2057374
//       // sortedData.run { first.zip(second).sumOf { (n1, n2) -> abs(n1 - n2) } }
//    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (total distance)") }
//    checkResult(2057374) { // [M3 523us]
//        2057375
//        // sortedData.run { first.zip(second).sumOf { (n1, n2) -> abs(n1 - n2) } }
//    }.let { (dt,result,check) -> println("${"[part 1]".inBrightYellow()} result: $result $check, dt: $dt (total distance)") }
//
//    println("Day $day\n-----\n")
    val cwd = System.getProperty("user.dir")
    println("Current working directory: $cwd")

    val example1 = """
3   4
4   3
2   5
1   3
3   9
3   3
"""
    val example = 0
    val inputData = when (example) {
        0 -> linesOf(day = day, year = 2024, path = ".")
        1 -> linesOf(data = example1)
        else -> throw RuntimeException("no data")
    }
    val examples = listOf("""xxx""")
    fun aocInput() = "data"
//    val configuration = object {
//        val day = 98
//        val year = 2024
//        val year_day = 2024_01
//        val example = 1
//        val lines = fetchAoCInputIfNeeded(day,year).let { when (example) {
//            0 -> linesOf(day = 1)
//            else -> linesOf(data = examples[example-1])
//        } }
//        val lines1 = linesOf(day,year, data = if (example==0) aocInput() else examples[example-1])
//        val dim = if (example==0) 71 else 7
//    }.apply {
//        lines.print(indent = 2, description = "Day: $day, Dim: $dim, Input:", take = 2)
//        println(year_day)
//    }

    //inputData.print(indent = 2, description = "input lines:", take = 6)

    val lineRegex = """(\d+)\s+(\d+)""".toRegex()
    // keep both lists at one place, no need for an extra class
    val sortedData = Pair(mutableListOf<Int>(), mutableListOf<Int>()).apply {
        for (line in inputData) {
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
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (similarity score)") }

    // exploit the sorted structure...
    timeResult { // [M3 810.125us]
        sortedData.run { first.sumOf { n1 -> n1 * second.run {
            binarySearch(n1).let { index ->
                if (index >= 0) 1 + countWhile(index - 1, -1, n1) + countWhile(index + 1, 1, n1) else 0
            }
        } } }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (alternative similarity score)") }

    // count before...
    timeResult { // [M3 1.769416ms]
        sortedData.run {
            val countMap = second.groupingBy { it }.eachCount()
            first.sumOf { n1 -> n1 * (countMap[n1] ?: 0) }
        }
    }.let { (dt,result) -> println("[part 2] result: $result, dt: $dt (alternative similarity score)") }

}
