# Sudoku solver

This is a low memory footprint Sudoku solver, written in Java. It solves any Sudoku puzzle that has a single possible solution (i.e. deterministic from the initial state).

The solver logic is implemented in bit manipulations (32-bit), so it can be easily ported to other languages. 

There is also a CLI interface where you can enter the full board, and the CLI automatically solves it. If it has no solution, you can manually enter values (or clear possibilities), the program solves the new state again. This helps the solving process.

Have fun, and let me know if you found it useful!

## About the solver logic

The Sudoku Solver uses a grid of cells, where each cell holds the information about the possible values. The cell value can be a fixed value, or it can contain any subset of 1-9 values. When a cell can contain only a single value, it becomes fixed.

The following rules are applied during the calculation (in order of calculation):

### Single cell value

When a cell can contain only one value, it is fixed. This is calculated when the board is changed (a value is cleared from a cell).

### Fixed value exclusions

When a value is fixed, other cells in the same row, column or box cannot contain the same value. It's calculated when the board is changed (a fixed value is set in a cell).

### Single possibilities

When a value can appear in only one cell in a row, column or a box, that value can be fixed. This is calculated when the solver ran out of previous moves.

### Exclusion by box and row (or by box and column)

When examining a box, if a value can appear only in one row in that box, that value is excluded from other cells in that row (i.e. from other boxes that this row intersects). The same applies for columns. This is calculated when the solver ran out of previous moves.

### Exclusion by complementers of row or columns

When a row intersects a box, the 3 cells may contain possible values that are not present in other cells of that row. It means that these values must be in the intersecting cells, nowhere else in that box. So, these must be cleared off from the complementing rows in that box. For columns, the same applies.

### Partitioning the values in row, column or box

This is the most complex move generation. Let's examine a row. If the first two cells can contain only two values altogether, all other cells in that row must not contain those two values. Otherwise, should those values be used in other cells, we couldn't set a value to one of the first two cells.

The above rule can be applied to any pair of fields, not just the first two fields. Further, it can be three, four, etc. number of fields and values, if they match. Lastly, it applies to all rows, all columns and all boxes. This is the most complex operation, since it must generate all real subsets of a set (up to 2^9 - 2 possibilities for each row, column or box). Therefore it's only calculated when the solver ran out of previous moves.

This calculation is a generalised case of the "Single possibility" calculation (single possibilities would be detected, too). The reason for having Single possibility check is that partitioning check is a heavy operation, but single possibilities are easy to detect, and in many cases single possibilities generate enough moves to proceed with the solution. 

## Unsolvable boards

Certain boards simply don't contain enough information to be solvable. This is a simple trick from Sudoku game creators, to encourage in-app purchase or use of virtual money. That's why I created the interactive mode.

In CLI mode, you can set up the board, specify each row with up to 9 characters (values 1-9, space for unknown, shorter line padded with empty on the right). After you set up, the solver solves the puzzle. If it does not solve fully, you can clear a value from a cell (e.g. you tried a value but it did not fit) or set a fixed value (e.g. you tried and succeeded, or used some in-app feature to reveal a cell).

## Implementation

The board is represented by an array of signed 32-bit integers (`int`s), 81 elements in a row-contiguous representation. That integer is a representation of a cell. Operations of the board and the cells are in `Board` and `Cell` classes.

There are two kinds of moves: Setting a fixed value to a cell, or clearing a possible value from a cell (possible values are called "floating" in the code). Each move is put to a queue when performed, the `SudokuSolver` reads the queue and executes these moves on the board, plus it does the "Single cell value" and the "Fixed value exclusions" checks. If a move is detected during these exclusions, that move is also put into the queue for further processing. When the board is solved, the loop stops, regardless the rest of the queue.

The `State` holds the state of the solution process, it holds the current state of the board and the moves to process. There are two queues, one for the `SET_FIXED` moves (this takes priority when processing) and one for `CLEAR_FLOAT` moves. When a move is registered in the state, it is added to the respective queue for processing.

If the queue is empty, but the board is still not solved, the `SudokuSolver` calls the `MoveGenerator` to generate more moves. The generated moves are put to the queue. If there are no possible moves, the loops stops with an unsolved state. That state can be resumed if some moves are added to the queue (the `SudokuSolverCli` works like that).

The `MoveGenerator` tries to generate moves in 3 phases ("Single possibilities", "Exclusion by box and row (or by box and column)" and "Partitioning the values in row, column or box"). If a phase yields some moves, those moves are returned. Each check has its own logic. Worth to note that in box and row exclusion multiple elements can be excluded from a row (up to 4). The `markExclude` method represents them as a 6-bit value in a 32-bit integer, with a stopper value of 0x3f - this is to spare with memory. Alternatives could be `List<Integer>` or `int[]` with variable length, but boxing/unboxing is painful, and maintaining length of an array is painful, too.

