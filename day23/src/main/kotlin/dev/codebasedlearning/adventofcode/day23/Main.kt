// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/23

package dev.codebasedlearning.adventofcode.day23

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
// 1: ..
"""
kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn
"""
)

fun main() {
    val story = object {
        val day = 23
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    var connections = story.lines.map { line -> line.split("-").let { it[0] to it[1] } }
    val network = mutableMapOf<String, MutableSet<String>>().apply {
        connections.forEach { (a, b) ->
            getOrPut(a) { mutableSetOf() }.add(b)
            getOrPut(b) { mutableSetOf() }.add(a)
        }
    }

    // part 1: solutions: 7 / 1302

    checkResult(1302) { // [M3 32.112750ms]
        val triangles = mutableSetOf<List<String>>()
        network.keys.forEach { k1 ->
            (network[k1]!! - k1).forEach { k2 ->
                (network[k2]!! - k1 - k2).filter{k1 in network[it]!!}.forEach { k3 ->
                    listOf(k1, k2, k3).run {
                        if (any { it.startsWith("t") }) triangles.add(sorted())
                    }
                }
            }
        }
        triangles.size
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (connected triangles)") }

    // part 2: solutions: [co, de, ka, ta] / [cb, df, fo, ho, kk, nw, ox, pq, rt, sf, tq, wi, xz]

    // task is to find cliques, see Bron+Kerbosch
    fun findLargestClique(current: Set<String>, candidates: Set<String>, excluded: Set<String>): Set<String> {
        if (candidates.isEmpty() && excluded.isEmpty()) { return current }

        val pivot = (candidates + excluded).first()
        var largest = emptySet<String>()
        (candidates - network[pivot]!!).forEach { node -> // non Neighbors
            val neighbors = network[node]!!
            val candidateClique = findLargestClique(current + node,
                candidates.intersect(neighbors), excluded.intersect(neighbors))
            if (candidateClique.size > largest.size) {
                largest = candidateClique
            }
        }
        return largest
    }

    checkResult(listOf("cb","df","fo","ho","kk","nw","ox","pq","rt","sf","tq","wi","xz")) { // [M3 26.444750ms]
        findLargestClique(mutableSetOf(),network.keys.toMutableSet(),mutableSetOf())
            .sorted()
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (largest clique)") }
}
