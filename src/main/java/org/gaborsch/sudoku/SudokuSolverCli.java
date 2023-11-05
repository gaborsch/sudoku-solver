package org.gaborsch.sudoku;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SudokuSolverCli {

	public static void main(String[] args) {
		new SudokuSolverCli().run();
	}

	private SudokuSolver solver;

	private SudokuSolverCli() {
		this.solver = new SudokuSolver(List.of());
	}

	private void run() {
		boolean lineCleared = true;
		try (Scanner scanner = new Scanner(System.in)) {
			String line = "help";
			while (!line.equalsIgnoreCase("x") && !line.equalsIgnoreCase("exit")) {
				try {
					if (line.equalsIgnoreCase("h") || line.equalsIgnoreCase("help")) {
						help();
					} else if (line.equalsIgnoreCase("b")) {
						scanner.nextLine();
						lineCleared = true;
						board(scanner);
						solve();
					} else if (line.equalsIgnoreCase("s") || line.equalsIgnoreCase("set")) {
						set(scanner);
						solve();
					} else if (line.equalsIgnoreCase("c") || line.equalsIgnoreCase("clear")) {
						clear(scanner);
						solve();
					} else {
						msg("Unknown command: '" + line + "'");
					}
				} catch (InputMismatchException e) {
					msg("Invalid command!");
				}

				if (solver.getCurrentBoard().isSolved()) {
					msg("Solved!");
					return;
				}
				if (!lineCleared) {
					scanner.nextLine();
				}

				System.out.print("> ");
				line = scanner.next("\\w+");
				lineCleared = false;
			}
		}
	}

	private static final Pattern ROW_PATTERN = Pattern.compile("^[1-9 ]{0,9}$");

	private void board(Scanner scanner) {
		String row;
		boolean match;
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 9; i++) {
			do {
				System.out.print("Row " + i + ": ");
				row = scanner.nextLine();
				match = ROW_PATTERN.matcher(row).matches();
				if (!match) {
					msg("Invalid values, try again!");
				}
			} while (!match);
			sb.append(row).append('\n');
		}
		solver.addMoves(BoardReader.getBoard(sb.toString()));
	}

	private void set(Scanner scanner) {
		int v = scanner.nextInt();
		int pos = scanPosition(scanner);
		if (pos >= 0) {
			if (Cell.isFloating(solver.getCurrentBoard().getCell(pos), v)) {
				solver.addMoves(List.of(Move.setValue(pos, v, "user input")));
			} else {
				msg("Invalid value!");
			}
		}
	}

	private void clear(Scanner scanner) {
		int v = scanner.nextInt();
		int pos = scanPosition(scanner);
		if (pos >= 0) {
			if (Cell.isFloating(solver.getCurrentBoard().getCell(pos), v)) {
				solver.addMoves(List.of(Move.clearFloat(pos, v, "user input")));
			} else {
				msg("Invalid value!");
			}
		}
	}

	private int scanPosition(Scanner scanner) {
		if (scanner.hasNext("[pP]")) {
			scanner.next("[pP]");
			int pos = scanner.nextInt();
			if (pos < 0 || pos > 80) {
				msg("Invalid position! Must be 0-80");
				return -1;
			}
			return pos;
		}
		int rn = scanner.nextInt();
		if (rn < 1 || rn > 9) {
			msg("Invalid row! Must be 1-9");
			return -1;
		}
		int cn = scanner.nextInt();
		if (cn < 1 || cn > 9) {
			msg("Invalid colunm! Must be 1-9");
			return -1;
		}
		return rn * 9 + cn - 10;
	}

	private void help() {
		msg("Sudoku Solver by gaborsch (c) 2023");
		msg("Commands:");
		msg("b, board");
		msg("    Enter a board row-by-row. Each line represents a row. Enter 9 characters: 1-9 are values, space is empty cell.");
		msg("s, set");
		msg("    set v rn cn - set value v to row rn and col cn. rn and cn can be 1-9");
		msg("    set v P pos  - set value v to position. pos can be 0-80 (pos 0 is row 1, col 1)");
		msg("c, clear");
		msg("    clear v rn cn - clear possible value v from row rn and col cn. rn and cn can be 1-9");
		msg("    clear v P pos  - clear possible value v from position. pos can be 0-80 (pos 0 is row 1, col 1)");
		msg("x, exit");
		msg("    Exit the program");
		msg("h, help");
		msg("    print this help");

	}

	private void solve() {
		solver.solve();
		msg(solver.getCurrentBoard().draw());
	}

	private void msg(String msg) {
		System.out.println(msg);

	}
}
