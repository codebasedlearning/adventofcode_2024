// (C) 2025 A.Voß, a.voss@fh-aachen.de, info@codebasedlearning.dev

package dev.codebasedlearning.adventofcode.day09

import dev.codebasedlearning.adventofcode.commons.input.linesOf
import dev.codebasedlearning.adventofcode.commons.timing.checkResult
import dev.codebasedlearning.adventofcode.commons.visualization.print

val examples = listOf(
"""
2333133121414131402
"""
)

data class DiskBlock(val id: Int, var len: Int) {
    val isData get() = id != -1
    val isFree get() = id == -1
}
fun dataBlockOf(id: Int, len: Int) = DiskBlock(id,len)
fun freeBlockOf(len: Int) = DiskBlock(-1,len)

fun List<DiskBlock>.mutableDeepCopy() = this.map { it.copy() }.toMutableList()

fun main() {
    val story = object {
        val day = 9
        val year = 2024
        val example = 0
        val lines = when (example) {
            0 -> linesOf(day, year, fetchAoCInput = true)
            else -> linesOf(input = examples[example-1])
        }
    }.apply {
        lines.print(indent = 2, description = "Day $day, Input:", take = 2)
    }

    var id = 0
    val diskBlocks = story.lines[0].map { it.digitToInt() }.mapIndexed  { i, len ->
        if (i%2==0) dataBlockOf(id++,len) else freeBlockOf(len) }

    fun checksum(blocks: List<DiskBlock>): Long = blocks.fold(0 to 0L) { (pos,sum), block ->
        if (block.isFree) Pair(pos+block.len,sum)
        // sum: id * pos..<pos+len
        else Pair(pos+block.len,sum + block.id * (block.len * pos + block.len*(block.len-1L)/2L)) // sum 0..n = n*(n+1)/2
    }.second

    // part 1: 1928 / 6401092019345

    checkResult(6401092019345) { // [M3 121.091583ms]
        val fragmentedBlocks = diskBlocks.mutableDeepCopy()
        while (true) {
            val i = fragmentedBlocks.indexOfLast { it.isData }
            val j = fragmentedBlocks.indexOfFirst { it.isFree  }
            if (i < 0 || j < 0) throw RuntimeException("corrupt data")

            if (i < j) break // all moved

            val currentBlock = fragmentedBlocks.removeAt(i) // remove block
            val freeBlock=fragmentedBlocks[j]

            if (currentBlock.len <= freeBlock.len) {
                fragmentedBlocks.add(j,currentBlock)        // insert here
                freeBlock.len -= currentBlock.len
            } else {
                fragmentedBlocks.removeAt(j)                // or split up
                fragmentedBlocks.add(j,dataBlockOf(currentBlock.id, freeBlock.len))
                fragmentedBlocks.add(dataBlockOf(currentBlock.id, currentBlock.len-freeBlock.len))
            }
        }
        checksum(fragmentedBlocks)
    }.let { (dt,result,check) ->
        println("[part 1] result: $result $check, dt: $dt (de-fragmented)")
    }

    // part 2: 2858 / 6431472344710

    checkResult(6431472344710) { // [M3 483.371959ms]
        val movedBlocks = diskBlocks.mutableDeepCopy()

        var i = movedBlocks.size-1
        while (true) {
            while (i >= 0 && movedBlocks[i].id == -1) --i   // next block to test
            if (i < 0) break
            val currentBlock = movedBlocks[i]

            val j = movedBlocks.indexOfFirst { it.id == -1 && it.len >= currentBlock.len }
            if (j == -1 || j > i) { --i; continue }
            val freeBlock = movedBlocks[j]

            if (currentBlock.len <= freeBlock.len) {
                movedBlocks.removeAt(i)
                movedBlocks.add(i,freeBlockOf(currentBlock.len))    // replace with free block
                // --i
                movedBlocks.add(j,currentBlock)                     // insert new
                // ++i
                freeBlock.len -= currentBlock.len                   // remaining space
            } else {
                --i
            }
        }
        checksum(movedBlocks)
    }.let { (dt,result,check) ->
        println("[part 2] result: $result $check, dt: $dt (moved blocks)")
    }
}
