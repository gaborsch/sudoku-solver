package org.gaborsch.sudoku;

import java.util.List;

public class SudokuSolver {
	
	boolean info = true;
	boolean trace = true;
	
	State state;

	public SudokuSolver(List<Move> initialSetup) {		
		state = new State(new Board(), initialSetup); 		
	}
	
	public Board solve() {
		Move m;
		int boardHash = 0;
		while ((m = state.getNextMove()) != null) {			
			Board b = state.getBoard();
			info(m.toString());
			if (m.getType() == Move.Type.SET_VALUE) {
				afterSetValue(b, m);
			} else if (m.getType() == Move.Type.CLEAR_FLOAT) {
				afterClearFloating(b, m);
			}
			if (boardHash != b.hashCode()) {
				trace(b.draw());
				boardHash = b.hashCode();
			}
		}
		
		return state.getBoard();
	}
	
	private void afterSetValue(Board b, Move m) {
		b.setFixedValue(m.getPos(), m.getValue());
		
		int rn = b.getRowNum(m.getPos());
		setClearFloatsTo(b,  Board.getRowPositions(rn), b.getRowValues(rn), m.getValue());
		int cn = b.getColNum(m.getPos());
		setClearFloatsTo(b,  Board.getColPositions(cn), b.getColValues(cn), m.getValue());
		int bn = b.getBoxNum(m.getPos());
		setClearFloatsTo(b,  Board.getBoxPositions(bn), b.getBoxValues(bn), m.getValue());
	}
	
	private void setClearFloatsTo(Board b, int[] positions, int[] cellValues, int value) {
		for (int i = 0; i < cellValues.length; i++) {
			if (! Cell.isFixed(cellValues[i])) {
				setClearFloatTo(b, positions[i], cellValues[i], value);
			}
		}
	}

	private void setClearFloatTo(Board b, int pos, int cell, int value) {
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
				state.addMove(Move.clearFloat(pos, value));
			} else {
				// if all others are cleared, save the new fixed value on the board
				b.setCell(pos, Cell.setValue(fixedValue));
				// and mark the set value for processing
				state.addMove(Move.setValue(pos, fixedValue));				
			}
		}
	}

	private void afterClearFloating(Board b, Move m) {
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
			state.addMove(Move.setValue(m.getPos(), fixedValue));
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
	
	public static final String SAMPLE_SETUP = 
			  "1 5      \n"
			+ " 2 7     \n"
			+ " 7   5  6\n"
			+ "6 1 874  \n"
			+ "  7 9 2 8\n"
			+ "         \n"
			+ "316 4    \n"
			+ "    3  94\n"
			+ "  9   5  \n";
	
	public static final String SAMPLE_SETUP2 = 
			  " 1  46 5 \n"
			+ "6    37 9\n"
			+ "  4  961 \n"
			+ "   6 85  \n"
			+ "  8  21 3\n"
			+ "34  5   7\n"
			+ "47 26 9  \n"
			+ "851 942  \n"
			+ " 2  17 3 \n";

	public static void main(String[] args) {
		List<Move> initialSetup = BoardReader.getBoard(SAMPLE_SETUP);
		SudokuSolver solver = new SudokuSolver(initialSetup);
		Board solution = solver.solve();
		System.out.println(solution.draw());
	}
	
	

}
