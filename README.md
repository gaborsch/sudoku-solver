# Sudoku solver

This is a low memory footprint Sudoku solver, written in Java. It solves any Sudoku puzzle that has a single possible solution (i.e. deterministic from the initial state).

The solver logic is implemented in bit manipulations (32-bit), so it can be easily ported to other languages. 

There is also a CLI interface where you can enter the full board, and the CLI automatically solves it. If it has no solution, you can manually enter values (or clear possibilities), the program solves the new state again. This helps the solving process.

Have fun, and let me know if you found it useful!




