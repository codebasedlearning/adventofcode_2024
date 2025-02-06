// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev
//
// see https://adventofcode.com/2024/day/12

package dev.codebasedlearning.adventofcode.day12

import dev.codebasedlearning.adventofcode.commons.geometry.Direction
import dev.codebasedlearning.adventofcode.commons.geometry.Position
import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.input.Lines
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print
import dev.codebasedlearning.adventofcode.commons.geometry.plus
import dev.codebasedlearning.adventofcode.commons.grid.Grid

import kotlin.math.max
import kotlin.math.min

enum class FieldStatus { Out, Same, Other }

val examples = listOf(
// 1: 140 / 80
"""
AAAA
BBCD
BBCC
EEEC
""",
// 2: 772 / 436
"""
OOOOO
OXOXO
OOOOO
OXOXO
OOOOO
""",
// 3: 1930 / 1206
"""
RRRRIICCFF
RRRRIICCCF
VVRRRCCFFF
VVRCCCJFFF
VVVVCJJCFE
VVIVCCJJEE
VVIIICJJEE
MIIIIIJJEE
MIIISIJEEE
MMMISSJEEE 
""",
// 4: 692 / 236
"""
EEEEE
EXXXX
EEEEE
EXXXX
EEEEE
""",
// 5: 1184 / 368
"""
AAAAAA
AAABBA
AAABBA
ABBAAA
ABBAAA
AAAAAA
"""
)

fun main() {
    val story = object {
        val day = 12
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    // some data structures
    data class Side(var status: FieldStatus, var side: Int = 0)
    data class Square(val pos: Position, val c: Char,
                      val sides: Map<Direction, Side>
                       = Direction.Cardinals.associateWith { dir -> Side(FieldStatus.Out) },
                      val corners: MutableMap<Direction, Boolean>
                       = Direction.InterCardinals.associateWith { dir -> false }.toMutableMap(),
    )
    data class Region(var sides: Int = 0, val squares: MutableSet<Square> = mutableSetOf()) {}

    fun Map<Direction, Side>.withPosition(position: Position)  = this.map { (dir,side) -> Triple(position+dir,dir,side) }
    fun Map<Direction, Side>.onlySame()  = this.filter { it.value.status == FieldStatus.Same }
    fun Map<Direction, Side>.onlyNotSame()  = this.filter { it.value.status != FieldStatus.Same }
    fun Map<Direction, Side>.isSame(dir: Direction)  = (this[dir]!!.status == FieldStatus.Same)
    fun Map<Direction, Side>.isNotSame(dir: Direction)  = !this.isSame(dir)
    fun Map<Direction, Side>.ifNotSameCopyFrom(dir: Direction, sideSquare:Square) {
        if (this.isNotSame(dir)) this[dir]!!.side = sideSquare.sides[dir]!!.side
    }

    fun <R> Lines.toGridWithPosition(block: (Position,Char) -> R) = Grid<R>().apply {
        this@toGridWithPosition.forEachIndexed { row, line ->
            add(line.mapIndexed { col, c -> block(Position(row,col),c) }.toMutableList())
        }
    }

    fun <T> Grid<T>.forEachWithPosition(block: (Position, T) -> Unit) {
        positions.forEach { pos -> block(pos, this[pos]) }
    }

    val garden = story.lines.toGridWithPosition { pos, c -> Square(pos, c) }.apply {
        forEachWithPosition { pos, square ->
            square.sides.forEach { (dir, side) ->
                if (this.isValid(pos+dir))
                    side.status = if (square.c == this[pos+dir].c) FieldStatus.Same else FieldStatus.Other
            }
        }
        forEachWithPosition { pos, square ->
            square.corners.keys.forEach { dir ->
                square.corners[dir] = dir.split().let { (d1, d2) ->
                    (square.sides.isNotSame(d1) && square.sides.isNotSame(d2))
                            || (square.sides.isSame(d1) && square.sides.isSame(d2)
                            && this.isValid(pos + dir) && this[pos + dir].c != square.c)
                }
            }
        }
    }

    // part 1: solution: - / 1421958

    val regions = mutableListOf<Region>()
    checkResult(1421958) { // [M3 27.074791ms]
        val floodPositions = garden.positions.toMutableSet()

        fun collect(start: Position?=null, startRegion: Region?=null) {
            val from = (start ?: floodPositions.first()).apply { floodPositions.remove(this) }
            val square = garden[from]
            val region = (startRegion ?: Region().also { r -> regions.add(r) }).apply { squares.add(square) }
            square.sides.onlySame().withPosition(from).forEach { (sidePos,_,_) ->
                if (sidePos in floodPositions) { collect(sidePos, region)}
            }
        }

        while (floodPositions.isNotEmpty()) {
            collect()
        }

        regions.sumOf { region ->
            region.squares.run { size * sumOf { it.sides.onlyNotSame().count() } }
        }
    }.let { (dt,result,check) -> println("[part 1] result: $result $check, dt: $dt (garden fences)") }

    /*  This is a somewhat strange solution. I just wanted to see how cumbersome it was to follow the borders
    and count the sides. Here each side gets a unique side-id and finally I count them.
    For all regions I consider only squares with fences and start somewhere. Then for each square I look at
    the neighbours (orthogonal) and if they "look" in the same direction, the side continues.
    The side-id propagates along the side and in some rare cases I meet a still process square.
    Then these two side-ids belong to the same side and instead of replacing them, I keep track and
    take it into account at the end.

    There are simpler solutions, such as counting line by line (and row by row by rotation).
    In this case you just have to keep track of when a new page starts and assign it to the correct region.

    Another observation can also be used: the number of sides is equal to the number of corners.
    Many people on Reddit have mentioned this approach. Here the tricky part is to count inner and outer corners.

    Update:
        With the right garden-preparation this corner approach is ridicules short. Just check for every square
        if it is an inner or outer corner. Then, finally, count them in your region - done :-)

    What we all have in common is that the code of part 2 is mostly ugly - so I am in good company :-)
    */

    // part 2: solution: - / 885394

    // see also V2 afterward

    checkResult(885394) { // [M3 9.355584ms]
        regions.sumOf { region ->
            var nextSideId = 0
            val sameSides = mutableSetOf<Pair<Int,Int>>()
            region.squares.forEach { square ->
                for (dir in square.sides.onlyNotSame().keys) {

                    fun checkAndCopy(sideDir: Direction) {
                        val id = square.sides[dir]!!.side
                        if (square.sides.isNotSame(sideDir)) {
                            if (id == 0) square.sides[dir]!!.side = ++nextSideId
                            return
                        }

                        val sideSquare = garden[square.pos + sideDir]
                        val sideId = sideSquare.sides[dir]!!.side

                        if (sideId > 0) {
                            if (id > 0) {
                                if (sideId != id) sameSides.add(Pair(min(id,sideId), max(id,sideId)))
                            } else
                                square.sides.ifNotSameCopyFrom(dir,sideSquare)
                        } else {
                            if (id == 0) square.sides[dir]!!.side = ++nextSideId
                            sideSquare.sides.ifNotSameCopyFrom(dir,square)
                        }
                    }

                    if (dir == Direction.Up || dir == Direction.Down) { //
                        checkAndCopy(Direction.Left)
                        checkAndCopy(Direction.Right)
                    }
                    if (dir == Direction.Left || dir == Direction.Right) {
                        checkAndCopy(Direction.Up)
                        checkAndCopy(Direction.Down)
                    }
                }
            }
            region.sides = nextSideId - sameSides.size
            region.squares.size * (region.sides )
        }

    }.let { (dt,result,check) -> println("[part 2 v1] result: $result $check, dt: $dt (garden sides)") }

    checkResult(885394) { // [M3 9.355584ms]
        regions.sumOf { region ->
            val sides = region.squares.sumOf { sq -> sq.corners.count { it.value } }
            region.squares.size * ( sides ) // region.sides
        }
    }.let { (dt,result,check) -> println("[part 2 v2] result: $result $check, dt: $dt (garden sides)") }
}
