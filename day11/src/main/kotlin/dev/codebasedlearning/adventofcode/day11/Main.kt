// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day11

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.parseNumbers
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import kotlin.math.pow
import kotlin.math.log10

class CounterMap<T>(
    private val delegate: MutableMap<T, Long> = mutableMapOf()
) : MutableMap<T,Long> by delegate {
    constructor(vararg iter:T):this() { addAll(iter.asIterable()) }
    constructor(iter: Iterable<T>):this() { addAll(iter) }
    constructor(map: CounterMap<T>):this() { addAll(map) }

    override operator fun get(key:T):Long =delegate.getOrDefault(key,0L)

    fun addAll(list: Iterable<T>) = apply { list.forEach { this[it] += 1 } }
    fun addAll(map: CounterMap<T>) = apply { map.forEach { this[it.key] += it.value  } }

    override fun toString():String = delegate.toString()
}

val examples = listOf(
// 1: ...
"""
0 1 10 99 999
""",
// 2: part 1: 55312
"""
125 17
"""
)

fun main() {
    val story = object {
        val day = 11
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val stonesOrig = story.lines[0].parseNumbers<Long>(' ')

    // the main idea is to count occurrences
    fun countStones(n: Int): Long {
        val stones = listOf(CounterMap<Long>(stonesOrig), CounterMap<Long>())
        var current = 0 // ping-pong

        repeat(n) {
            val nextStones = stones[1 - current]
            nextStones.clear()

            for ((id, count) in stones[current]) {
                if (id == 0L) {
                    nextStones[1L] += count
                } else {
                    // it feels a little bit better... and, id is not 0 here!
                    val numDigits = log10(id.toDouble()).toInt() + 1
                    if (numDigits % 2 == 0) {
                        val divisor = 10.0.pow(numDigits / 2).toLong()
                        nextStones[id / divisor] += count
                        nextStones[id % divisor] += count
                    } else {
                        nextStones[id * 2024L] += count
                    }
                    /* val str = id.toString()
                    val len = str.length
                    if (len % 2 == 0) {
                        nextStones[str.substring(0, len / 2).toLong()] += count  // or take()
                        nextStones[str.substring(len / 2).toLong()] += count     // or drop()
                    } else {
                        nextStones[id * 2024L] += count
                    } */
                }
            }
            current = 1 - current
        }
        return stones[current].values.sum()
    }

    // part 1: solution: 193607

    checkResult(193607) { // [M3 2.876ms]
        countStones(25)
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (stones 25)") }

    // part 2: solution: 229557103025807

    checkResult(229557103025807) { // [M3 29.627292ms]
        countStones(75)
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (stones 75)") }
}
