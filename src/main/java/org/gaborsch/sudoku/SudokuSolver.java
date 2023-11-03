package org.gaborsch.sudoku;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SudokuSolver {
	
	boolean info = true;
	boolean trace = false;

	private State state;
	private Set<Move> processedMoves = new HashSet<>();

	public SudokuSolver(List<Move> initialSetup) {
		state = new State(new Board(), initialSetup);
	}
	
	public void addMoves(List<Move> moves) {
		state.addMoves(moves);
	}

	public Board solve() {
		int boardHash = 0;
		do {
			while (!state.getBoard().isSolved() && state.hasNextMove()) {
				Move m = state.getNextMove();
				Board b = state.getBoard();
				if (!processedMoves.contains(m)) {
					info(m.toString());
					processedMoves.add(m);
				}
				if (m.getType() == Move.Type.SET_VALUE) {
					doSetValue(b, m);
				} else if (m.getType() == Move.Type.CLEAR_FLOAT) {
					doClearFloating(b, m);
				}
				if (trace && boardHash != b.hashCode()) {
					trace(b.draw());
					boardHash = b.hashCode();
				}
			}
			if(! state.getBoard().isSolved()) {
				info("Generating moves...");
				new MoveGenerator(state).generateMoves();
			}
		} while (!state.getBoard().isSolved() && state.hasNextMove());

		return state.getBoard();
	}
	
	public Board getCurrentBoard() {
		return state.getBoard();
	}
	
	private void doSetValue(Board b, Move m) {
		b.setFixedValue(m.getPos(), m.getValue());
		int rn = m.getRowNum();
		setClearFloatsTo(b, Board.getRowPositions(rn), b.getRowValues(rn), m.getValue(), "clearing " + m.getRowCoord() + " for " + m.getCoords());
		int cn = m.getColNum();
		setClearFloatsTo(b, Board.getColPositions(cn), b.getColValues(cn), m.getValue(), "clearing " + m.getColCoord() + " for " + m.getCoords());
		int bn = m.getBoxNum();
		setClearFloatsTo(b, Board.getBoxPositions(bn), b.getBoxValues(bn), m.getValue(), "clearing " + m.getBoxCoord() + " for " + m.getCoords());
	}

	private void setClearFloatsTo(Board b, int[] positions, int[] cellValues, int value, String note) {
		for (int i = 0; i < cellValues.length; i++) {
			if (!Cell.isFixed(cellValues[i])) {
				setClearFloatTo(b, positions[i], cellValues[i], value, note);
			}
		}
	}

	private void setClearFloatTo(Board b, int pos, int cell, int value, String note) {
		// if this value has not been cleared yet
		if (Cell.isFloating(cell, value)) {
			// clear float value
			int newCell = Cell.clearFloating(cell, value);
			// check if there is only 1 candidate left
			int fixedValue = Cell.findValue(newCell);

			if (fixedValue == 0) {
				// if more than 1, then save the cleared value
				b.setCell(pos, newCell);
				// and mark the clear float for processing
				state.addMove(Move.clearFloat(pos, value, note));
			} else {
				// if all others are cleared, save the new fixed value on the board
				b.setCell(pos, Cell.setValue(fixedValue));
				// and mark the set value for processing
				state.addMove(Move.setValue(pos, fixedValue, "single cell value found during fixed value exclusion"));
			}
		}
	}

	private void doClearFloating(Board b, Move m) {
		// is it fixed? then nothing to do
		if (Cell.isFixed(b.getCell(m.getPos()))) {
			return;
		}

		// clear the floating value if it has not happened yet
		int cell = b.clearFloating(m.getPos(), m.getValue());

		// check if there's only one floating left, if yes, set that value
		int fixedValue = Cell.findValue(cell);
		if (fixedValue > 0) {
			b.setCell(m.getPos(), Cell.setValue(fixedValue));
			state.addMove(Move.setValue(m.getPos(), fixedValue, "single cell value"));
		} else {
			// check if only one possibility remained in the box / row / column
			checkIfHasOnlyOneFloatingAtPositions(m.getValue(), Board.getRowPositions(m.getRowNum()), m.getRowCoord());
			checkIfHasOnlyOneFloatingAtPositions(m.getValue(), Board.getColPositions(m.getColNum()), m.getColCoord());
			checkIfHasOnlyOneFloatingAtPositions(m.getValue(), Board.getBoxPositions(m.getBoxNum()), m.getBoxCoord());
		}
	}

	private void checkIfHasOnlyOneFloatingAtPositions(int value, int[] positions, String type) {
		int count = state.getBoard().countFloatingValue(value, positions);
		if (count == 1) {
			int pos = state.getBoard().getFirstFloatingValuePosition(value, positions);
			state.addMove(Move.setValue(pos, value, "only one left in " + type));
		}
	}

	void info(String msg) {
		if (info) {
			System.out.println(msg);
		}
	}

	void trace(String msg) {
		if (trace) {
			System.out.println(msg);
		}
	}
	
	public static final String SAMPLE_SETUP2 = 
			  "1 5      \n"
			+ " 2 7     \n"
			+ " 7   5  6\n"
			+ "6 1 874  \n"
			+ "  7 9 2 8\n"
			+ "         \n"
			+ "316 4    \n"
			+ "    3  94\n"
			+ "  9   5  \n";
	
	public static final String SAMPLE_SETUP1 = 
			  " 1  46 5 \n"
			+ "6    37 9\n"
			+ "  4  961 \n"
			+ "   6 85  \n"
			+ "  8  21 3\n"
			+ "34  5   7\n"
			+ "47 26 9  \n"
			+ "851 942  \n"
			+ " 2  17 3 \n";

	public static final String SAMPLE_SETUP3 = 
			  "     8 6 \n"
			+ " 15    2 \n"
			+ "  2      \n"
			+ "    5  1 \n"
			+ "4       7\n"
			+ "    62  4\n"
			+ "5   2    \n"
			+ "  89  7  \n"
			+ " 46  78  \n";
	
	public static final String SAMPLE_SETUP4 = 
			  "6       5\n"
			+ "   34  6 \n"
			+ "     8   \n"
			+ "87       \n"
			+ "  98    2\n"
			+ "  6   1 9\n"
			+ "7   19   \n"
			+ "   73    \n"
			+ "13   5 4 \n";
	
	public static final String SAMPLE_SETUP5 = 
			  "      3 4\n"
			+ "   924   \n"
			+ "4 58 3 6 \n"
			+ "2 46  7  \n"
			+ "   5    8\n"
			+ "  7 3    \n"
			+ "       2 \n"
			+ "83     4 \n"
			+ "6    9  1\n";


	public static final String SAMPLE_SETUP6 = 
			  " 8    1  \n"
			+ " 9 2  7  \n"
			+ " 57   83 \n"
			+ "928374561\n"
			+ "7 3  54  \n"
			+ "4 59     \n"
			+ "672 3 9 4\n"
			+ "831  9   \n"
			+ "549    1 ";
	
	
	public static void main(String[] args) {
		List<Move> initialSetup = BoardReader.getBoard(SAMPLE_SETUP6);
		SudokuSolver solver = new SudokuSolver(initialSetup);
		Board solution = solver.solve();
		System.out.println(solution.draw());
	}

}
