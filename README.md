# Sudoku solver

This is a low memory footprint Sudoku solver, written in Java. It solves any Sudoku puzzle that has a single possible solution (i.e. deterministic from the initial state).

The solver logic is implemented in bit manipulations (32-bit), so it can be easily ported to other languages. 

There is also a CLI interface where you can enter the full board, and the CLI automatically solves it. If it has no solution, you can manually enter values (or clear possibilities), the program solves the new state again. This helps the solving process.

Have fun, and let me know if you found it useful!

## About the solver logic

The Sudoku Solver uses a grid of cells, where each cell holds the information about the cell. The cell value can be a fixed value, or it can contain any subset of 1-9 values. When a cell can contain only a single value, it becomes fixed.

The following rules are applied during the calculation (in order of calculation):

### Single cell value

When a cell can contain only one value, it is fixed. This is calculated when the board is changed (a value is cleared from a cell).

### Fixed value exclusions

When a value is fixed, other cells in the same row, column or box cannot contain the same value. It's calculated when the board is changed (a fixed value is set in a cell).

### Single possibilities

When a value can appear in just one cell in a row, column or box, that value is fixed. This is calculated when the solver ran out of previous moves.

### Exclusion by box and row (and by box and column)

When examining a box, and a value can appear only in one row in that box, that value is excluded from other cells in that row (i.e. from other boxed that row crosses). The same applies for columns. This is calculated when the solver ran out of previous moves.

### Partitioning the values in row, column or box.

This is the most complex move generation. Let's examine a row. If the first two cells can contain only two values altogether, all other cells in that row must not contain those two values. Otherwise, should those values be used in other cells, we couldn't set a value to one of the first two cells.

The above rule can be applied to any pair of fields, not just the first two fields. Further, it can be three, four, etc. number of fields and values, if they match. Lastly, it applies to all rows, all columns and all boxes. This is the most complex operation, since it must generate all real subsets of a set (up to 2^9 - 2 possibilities for each row, column or box). Therefore it's only calculated when the solver ran out of previous moves.

This calculation is a generalised case of the "Single possibility" calculation (single possibilities would be detected, too). The reason for having Single possibility check is that partitioning check is a heavy operation, but single possibilities are easy to detect, and it's enough in many cases to proceed the solution. 

## Unsolvable boards

Certain boards simply don't contain enough information to be solvable. This is a simple trick from Sudoku game creators, to encourage in-app purchase or use of virtual money. That's why I created the interactive mode.

In CLI mode, you can set up the board, specify each row with up to 9 characters (values 1-9, space for unknown, shorter line padded with empty on the right). After you set up, the solver solves the puzzle. If it does not solve fully, you can clear a value from a cell (e.g. you tried a value but it did not fit) or set a fixed value (e.g. you tried and succeeded, or used some in-app feature to reveal a cell).


