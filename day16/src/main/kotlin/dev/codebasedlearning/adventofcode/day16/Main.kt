// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day16

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.geometry.Step
import dev.codebasedlearning.adventofcode.commons.grid.toGrid
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.plus

typealias Path = MutableList<Position>

data class ScoredPath(val path: Path, val score: Int)
data class Node(val dirPos: Step, val scoredPath: ScoredPath)
fun nodeOf(pos: Position, dir: Direction, path: Path, score: Int)
        = Node(Step(pos,dir),ScoredPath(path,score))

val examples = listOf(
// 1: 7036 / 45
    """
###############
#.......#....E#
#.#.###.#.###.#
#.....#.#...#.#
#.###.#####.#.#
#.#.#.......#.#
#.#.#####.###.#
#...........#.#
###.#.#####.#.#
#...#.....#.#.#
#.#.#.###.#.#.#
#.....#...#.#.#
#.###.#.#.#.#.#
#S..#.....#...#
###############
""",
// 2: 11048 / 64
    """
#################
#...#...#...#..E#
#.#.#.#.#.#.#.#.#
#.#.#.#...#...#.#
#.#.#.#.###.#.#.#
#...#.#.#.....#.#
#.#.#.#.#.#####.#
#.#...#.#.#.....#
#.#.#####.#.###.#
#.#.#.......#...#
#.#.###.#####.###
#.#.#...#.....#.#
#.#.#.#####.###.#
#.#.#.........#.#
#.#.#.#########.#
#S#.............#
#################            
""",
// 3: 2007 / 8 (debug example)
    """
########
#.....E#
#.#.##.#
#S#....#
########
"""
)

fun main() {
    val story = object {
        val day = 16
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    val maze = story.lines.toGrid()

    maze.print(description = "original:", separator = "")
    var startPos = maze.positions.find { maze[it] == 'S' }!!.also { maze[it] = '.' }
    var endPos = maze.positions.find { maze[it] == 'E' }!!.also { maze[it] = '.' }

    println("\nstartPos: $startPos, endPos: $endPos\n")

    fun findAllRoutes(): List<ScoredPath> = mutableListOf<ScoredPath>().apply {
        val visited = mutableMapOf<Step, Int>()
        val queue = ArrayDeque<Node>()

        queue.add(nodeOf(startPos, Direction.Right,mutableListOf(startPos),0))
        while (queue.isNotEmpty()) {
            val (reindeer, current) = queue.removeFirst()
            when {
                reindeer.pos == endPos -> { this.add(current); continue }
                reindeer in visited && visited[reindeer]!! < current.score -> continue
                else -> {
                    visited[reindeer] = current.score
                    for (dir in Direction.Cardinals) { // LineDirection.Cross
                        if (reindeer.dir.isOpposite(dir)) continue
                        val nextPos = reindeer.pos+dir

                        if (maze[nextPos]=='.' && (nextPos !in current.path)) { // no border checks
                            queue.add(if (dir==reindeer.dir)
                                nodeOf(nextPos,dir,current.path.toMutableList().also { it.add(nextPos) },current.score+1)
                            else
                                nodeOf(reindeer.pos,dir,current.path.toMutableList(),current.score+1000) // just turn
                            )
                        }
                    }
                }
            }
        }
    }

    var minScore = 0

    // part 1: solutions: - / 83432

    checkResult(83432) { // [M3 233.724084ms]
        findAllRoutes().minBy { it.score }.score.apply { minScore = this }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (Reindeer maze)") }

    // part 2: solutions: - / 467

    checkResult(467) { // [M3 167.095667ms]
        findAllRoutes().filter { it.score == minScore }
            .flatMap { it.path }.toSet()
            .size
    }.let { (dt,result,check) -> println("[part 2] result: $result $check, dt: $dt (best seats)") }
}
