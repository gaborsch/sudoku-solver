# Sudoku solver

This is a low memory footprint Sudoku solver, written in Java.

The solver logic is implemented in bit manipulations (32-bit), so it can be easily ported to other languages. Still there is room for more memory optimisation, e.g reusing a few buffers.

There is also a CLI interface where you can enter the full board, and the CLI automatically solves it. If it's not possible to solve, you can manually enter values (or clear possibilities), the program solves the new state again. 

Have fun, and let me know if you found it useful!




