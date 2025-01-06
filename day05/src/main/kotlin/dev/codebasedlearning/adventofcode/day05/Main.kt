// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day05

import dev.codebasedlearning.adventofcode.commons.checkResult
import dev.codebasedlearning.adventofcode.commons.linesOf
import dev.codebasedlearning.adventofcode.commons.parseNumbers
import dev.codebasedlearning.adventofcode.commons.print
import dev.codebasedlearning.adventofcode.commons.splitHeaderBlock
import dev.codebasedlearning.adventofcode.commons.toBlocks

val examples = listOf(
"""
47|53
97|13
97|61
97|47
75|29
61|13
75|53
29|13
97|29
53|29
61|53
97|53
61|29
47|13
75|47
97|75
47|61
75|61
47|29
75|13
53|13

75,47,61,53,29
97,61,53,29,13
75,29,13
75,97,47,61,53
61,13,29
97,13,75,29,47
"""
)

fun main() {
    val story = object {
        val day = 5
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val (ruleLines, updateLines) = story.lines.toBlocks().splitHeaderBlock(n = 1).run { first[0] to second[0] }

    val rules = ruleLines
        .map { line -> line.parseNumbers<Int>('|').let { (a, b) -> a to b } }
        .groupBy({ it.first }, { it.second })
        .mapValues { (_, values) -> values.toSet() }
    val updates = updateLines.map { line -> line.parseNumbers<Int>(',') }

    fun doesBreak(n: Int, m: Int) = rules[m]?.contains(n)==true // or: n in rules[m]!!
    fun List<Int>.isCorrect(n:Int, from:Int = 0) = subList(from,size).all { m -> !doesBreak(n, m) }
    fun List<Int>.isCorrect() = withIndex().all { (index, n) -> isCorrect(n, from = index+1) }

    // part 1: solutions: 143 / 6051

    val wrongUpdates = mutableListOf<List<Int>>()
    checkResult(6051) { // [M3 2.074584ms]
        updates.sumOf { update ->
            if (update.isCorrect()) { update[update.size/2] }
            else { wrongUpdates.add(update); 0 }
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (...)") }

    // part 2: solutions: 123 / 5093

    checkResult(5093) { // [M3 6.413750ms]
        wrongUpdates.sumOf { update ->
            buildList {
                val pages = update.toMutableList()
                // assuming there _is_ a solution...
                while (pages.isNotEmpty()) {
                    pages.removeFirst().let { n-> (if (pages.isCorrect(n)) this else pages).add(n) }
                }
            }.run { this[this.size/2] } // btw. we could have stopped at the mid-element
        }
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (...)") }
}
