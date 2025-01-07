// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/17

package dev.codebasedlearning.adventofcode.day17

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.input.toBlocks
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
// 1
"""
Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
""",
// 2
"""
Register A: 0
Register B: 0
Register C: 9

Program: 2,6
""",
// 3
"""
Register A: 10
Register B: 0
Register C: 0

Program: 5,0,5,1,5,4
""",
// 4
""" 
Register A: 2024
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0
""",
// 5
"""
Register A: 0
Register B: 29
Register C: 0

Program: 1,7
""",
// 6
"""
Register A: 0
Register B: 2024
Register C: 43690

Program: 4,0
""",
// 7
"""
Register A: 117440
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0
"""
)

fun main() {
    val story = object {
        val day = 17
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

    val inputRegs = blocks[0].map { it.substringAfter(": ").toLong() }
    val inputProgramm = blocks[1][0].substringAfter(": ").parseNumbers<Int>(',')

    class ThreeBitComputer(val registers: List<Long>, val programm: List<Int>) {
        fun run(initA: Long = -1): List<Int> = mutableListOf<Int>().apply {
            var regA = if (initA >= 0) initA else registers[0]
            var regB = registers[1]
            var regC = registers[2]
            var ip = 0
            while (ip in programm.indices) {
                val (opcode, operand) = programm[ip] to programm[ip + 1].toLong()
                val combo = when (operand) {
                    in 0L..3L -> operand
                    4L -> regA
                    5L -> regB
                    6L -> regC
                    7L -> 0L
                    else -> throw RuntimeException("unknown operand: $operand")
                }
                when (opcode) {
                    0 -> { regA = (regA / (1L shl combo.toInt())); ip += 2 }
                    1 -> { regB = regB xor operand; ip += 2 }
                    2 -> { regB = combo % 8; ip += 2 }
                    3 -> { if (regA != 0L) { ip = operand.toInt() } else { ip += 2 } }
                    4 -> { regB = regB xor regC; ip += 2 }
                    5 -> { add((combo % 8L).toInt()); ip += 2 }
                    6 -> { regB = (regA / (1L shl combo.toInt())).toLong(); ip += 2 }
                    7 -> { regC = (regA / (1L shl combo.toInt())).toLong(); ip += 2 }
                    else -> throw RuntimeException("unknown opcode: $opcode")
                }
            }
        }
    }

    // part 1: solutions: '4,6,3,5,6,3,5,2,1,0' / '2,0,4,2,7,0,1,0,3'

    checkResult("'2,0,4,2,7,0,1,0,3'") { // [M3 562.584us]
        ThreeBitComputer(inputRegs, inputProgramm).run().joinToString(",",prefix="'",postfix="'")
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (program output)") }

    // part 2: solutions: - / 265601188299675

    checkResult(265601188299675) { // [M3 10.832833ms]
        fun Long.replace8(digit: Int, at: Int, ): Long = (this and ((7L shl at*3).inv())) + ((digit % 8).toLong() shl at*3)
        val computer = ThreeBitComputer(inputRegs, inputProgramm)

        var full7 = (1L shl (3*(inputProgramm.size)))-1L    // visualize it
        val candidates = mutableSetOf(full7)                // we need to check all
        for (i in inputProgramm.size-1 downTo 0) {          // start at highest 8-digit
            val digit = inputProgramm[i]
            val newCandidates = mutableSetOf<Long>()
            for (c in candidates) {
                for (j in 0..<8) {
                    val initA = c.replace8(j, at = i)
                    val res = computer.run(initA).toMutableList()
                    // of course... a leading zero
                    if (res.size<inputProgramm.size) res.addAll(List(inputProgramm.size - res.size) { 0 })
                    if (res[i] == digit) newCandidates.add(initA)
                }
            }
            candidates.clear(); candidates.addAll(newCandidates)
        }

//        if (false) {
//            // want see all solutions?
//            println("\ninput program: $inputProgramm")
//            for (c in candidates) {
//                val res = computer.run(c)
//                println("check ($c) 0o${c.toString(8)} -> $res -> ${res == inputProgramm}")
//            }
//        }
        candidates.firstOrNull() ?: -1
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (lowest positive initial value)") }
}
