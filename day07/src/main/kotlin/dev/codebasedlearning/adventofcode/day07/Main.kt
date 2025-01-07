// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day07

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20
"""
)

enum class Operators(val operators: String) {
    Part1("+*"),
    Part2("+*|");

    fun calc(step: Char, r: Long, num: Long) = when (step) {
        '+' -> num + r
        '*' -> num * r
        '|' -> "$r$num".toLong()
        else -> 0
    }

    fun combine(length: Int) = generateCombinations(this.operators, length)

    companion object {
        private val opsCache = Operators.entries.map { operators ->
            mutableMapOf<Int,List<String>>()
        }
    }

    private fun generateCombinations(ops: String, length: Int): List<String> = when {
        opsCache[this.ordinal].containsKey(length) -> opsCache[this.ordinal][length]!!
        length == 0 -> listOf("")
        else -> generateCombinations(ops, length - 1).flatMap { ops.map { c -> "$it$c" } }
            .apply { opsCache[this@Operators.ordinal][length] = this }
    }
}

data class Equation(val result: Long, val numbers: List<Long>)

fun main() {
    val story = object {
        val day = 7
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val equations = story.lines.map { line ->
        line.split(":").let { (result, numbers) -> Equation(result.toLong(), numbers.parseNumbers<Long>(' ')) }
    }

    fun calibrate(operators: Operators): Long = equations.sumOf { equation ->
        val ops = operators.combine(equation.numbers.size - 1)
        if (ops.any { op -> equation.numbers.reduceIndexed { index, r, num ->
                if (index == 0) num else operators.calc(op[index - 1], r, num) } == equation.result })
            equation.result
        else
            0L
    }

    // part 1: solutions: 3749 / 2654749936343

    checkResult(2654749936343) { // [M3 32.093709ms]
        calibrate(Operators.Part1)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (calibration result)") }

    // part 2: solutions: 11387 / 124060392153684

    checkResult(124060392153684) { // [M3 1.258522167s]
        calibrate(Operators.Part2)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (calibration result with concatenation)") }
}
