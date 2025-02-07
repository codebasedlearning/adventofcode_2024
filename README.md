# adventofcode_2024

All AoC 2024 solutions.

This is the new CBL repo with the revised AoC code 2024. I've added a few solution variants 
to my code based on discussions and some very clever ideas on Reddit - thanks to Eric 
and everyone else for this year!

(This is a new GitHub account, AoC 2024 has been done as 'Rowlf'.)

---

Characterise problems and solutions in terms of programming language features
and algorithmic requirements, with a view to learning a new language.

All solutions involve reading data from a text file 'input.txt' and string manipulation.
More typical ingredients are (data) classes to model small pieces of data with
some properties, and typical collections such as lists/arrays, sets and maps.
Tasks with 2D grid data use 2D arrays and, if modelled with classes, may also use
indexes and operators.

## 2024

### Day 01 Historian Hysteria
- data '3 4'
- extract numbers, string manipulation, maybe regular expressions
- easy

### Day 02 Red-Nosed Reports
- data '7 6 4 2 1'
- lists
- easy

### Day 03 Mull It Over
- data 'xmul(2,4)%&mul[3,7]!@^do_not_mul(5,5)+mul(32,64]then(mul(11,8)mul(8,5))'
- regular expressions
- easy

### Day 04 Ceres Search
- grid data 'MMMSXXMASM'
- (2d)arrays, classes, index-manipulations
- easy

### Day 05 Print Queue
- data '47|53 // 97|13 // 75,47,61,53,29'
- maps
- easy

### Day 06 Guard Gallivant
- grid data '....#.....'
- (2d)arrays, index-manipulations, math (graph)
- easy/medium

### Day 07 Bridge Repair
- data '190: 10 19'
- classes, enums
- easy/medium (idea)

### Day 08 Resonant Collinearity
- grid data '........A...'
- (2d)arrays, index-manipulations
- easy

### Day 09 Disk Fragmenter
- data '2333133121414131402'
- classes
- easy/medium (complexity)

### Day 10 Hoof It
- grid data '...2... // 6543456'
- (2d)arrays, index-manipulations, math (graph)
- easy

### Day 11 Plutonian Pebbles
- data '0 1 10 99 999'
- maps
- easy

### Day 12 Garden Groups
- grid data 'AAAA // BBCD'
- (2d)arrays, index-manipulations, classes, math (graph)
- medium (part 1)/hard (part 2)

### Day 13 Claw Contraption
- data 'Button B: X+22, Y+67 // Prize: X=8400, Y=5400'
- classes, math (lin.algebra)
- easy

### Day 14 Restroom Redoubt
- data 'p=0,4 v=3,-3'
- (2d)arrays, index-manipulations, classes
- medium (complexity)

### Day 15 Warehouse Woes
- grid data '##@.O..#'
- (2d)arrays, index-manipulations
- hard (complexity)

### Day 16 Reindeer Maze
- grid data '#.#.###.#.###.#'
- (2d)arrays, index-manipulations, math (graph)
- medium (graph)

### Day 17 Chronospatial Computer
- data 'Register C: 0 // Program: 0,1,5,4,3,0'
- classes, long numbers
- easy/medium

### Day 18 RAM Run
- data '5,4 // 4,2'
- math (graph)
- medium (idea)

### Day 19 Linen Layout
- data 'r, wr, b, g, bwu, rb, gb, br // brwrr'
- algo (dynamic programming)
- medium/hard (idea)

### Day 20 Race Condition
- grid data '#.#.#.#.#.###.#'
- (2d)arrays, index-manipulations, math (graph)
- medium/hard

### Day 21 Keypad Conundrum
- data '029A // 980A'
- algo (dynamic programming)
- hard (idea)

### Day 22 Monkey Market
- data '1 // 10 // 100 // 2024'
- maps, long numbers
- medium (task description)

### Day 23 LAN Party
- data 'kh-tc // qp-kh'
- maps, math (graph)
- medium/hard (graph)

### Day 24 Crossed Wires
- data 'y02: 0 // x00 AND y00 -> z00'
- maps, classes
- easy (part 1), devil (part 2, idea)

### Day 25 Code Chronicle
- grid data '.#.#.'
- maps
- easy
