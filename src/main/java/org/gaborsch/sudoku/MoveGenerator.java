package org.gaborsch.sudoku;

public class MoveGenerator {

	private State state;
	private Board board;

	public MoveGenerator(State state) {
		this.state = state;
		this.board = state.getBoard();
	}

	public void generateMoves() {
		generateMovesForRowColExclusion();
		if (! state.hasNextMove()) {
			generateMovesForSinglePossibilities();
		}
		if (! state.hasNextMove()) {
			generateMovesForPartitioning();
		}
			
	}
	
	private static final int FIXED_MARKER_IN_FLOATINGS = 1<<16; 

	private void generateMovesForRowColExclusion() {
		int[][] floatingsByRowAndBox = new int[9][9];
		int[][] floatingsByColAndBox = new int[9][9];
		for (int pos = 0; pos < Board.BOARD_SIZE; pos++) {
			int floatings = Cell.getFloatings(board.getCell(pos));
			int fixed = Cell.isFixed(board.getCell(pos)) ? FIXED_MARKER_IN_FLOATINGS : 0;
			floatingsByRowAndBox[Board.getRowNum(pos)][Board.getBoxNum(pos)] |= floatings | fixed;
			floatingsByColAndBox[Board.getColNum(pos)][Board.getBoxNum(pos)] |= floatings | fixed;
		}
	
		// check each value
		for (int v=1; v <= 9; v++) {
			// check rows
			for (int rn=0; rn < 9; rn+=3) {
				long excludeRowBoxPairs = checkExclusion(v, 
						floatingsByRowAndBox[rn], 
						floatingsByRowAndBox[rn+1], 
						floatingsByRowAndBox[rn+2]);
				while ((excludeRowBoxPairs & 0xff) != 0xff) {
					int excludeRn = (int) (rn + ((excludeRowBoxPairs & 0xf0) >> 4));
					int excludeBn = (int) (excludeRowBoxPairs & 0xf);
					excludeRowBoxPairs >>= 8;
					// exclude value 'v' from row and box
					int[] excludePositions = Board.intersectBoxRow(excludeBn, excludeRn);
					excludeFloatings(v, excludePositions, "box and row "+excludeRn);
				}
				
			}
			// check cols
			for (int cn=0; cn < 9; cn+=3) {
				long excludeColBoxPairs = checkExclusion(v, 
						floatingsByColAndBox[cn], 
						floatingsByColAndBox[cn+1], 
						floatingsByColAndBox[cn+2]);
				while ((excludeColBoxPairs & 0xff) != 0xff) {
					int excludeCn = (int) (cn + ((excludeColBoxPairs & 0xf0) >> 4));
					int excludeBn = (int) (excludeColBoxPairs & 0xf);
					excludeColBoxPairs >>= 8;
					// exclude value 'v' from row and box
					int[] excludePositions = Board.intersectBoxCol(excludeBn, excludeCn);
					excludeFloatings(v, excludePositions, "box and col "+excludeCn);
				}
				
			}
		}
	
	}


	
	private long checkExclusion(int v, int[] s0, int[] s1, int[] s2) {
		long retval = 0xff;
		// find out possible boxes (always 3)
		int[] b = new int[3];
		for(int bn=0, bi = 0; bi<3; bn++) {
			if (s0[bn] + s1[bn] + s2[bn] > 0) {
				b[bi++] = bn;
			}
		}
		
		// iterate through boxes
		for(int bi=0; bi < b.length; bi++) {
			int bn = b[bi];
			// boxes in this series
			// count of series that may contain v
			int s0n = (s0[bn] >> v) & 1;
			int s1n = (s1[bn] >> v) & 1;
			int s2n = (s2[bn] >> v) & 1;
			// does it only appears in 1 series in the box?
			if (s0n + s1n + s2n == 1) {
				// then exclude other boxes in that series
				int sn = s1n + 2*s2n;
				if (bn != b[0]) { retval = markExclude(retval, sn, b[0]); }
				if (bn != b[1]) { retval = markExclude(retval, sn, b[1]); }
				if (bn != b[2]) { retval = markExclude(retval, sn, b[2]); }
			}
		}
		
		return retval;
	}	
	
	private long markExclude(long retval, int sn, int bn) {
		return (retval << 8) | (sn << 4) | bn;
	}	
	
	
	/*
	 * exclude value 'v' from given positions, mark clearing if needed
	 */
	private void excludeFloatings(int v, int[] excludePositions, String note) {
		for (int i = 0; i < excludePositions.length; i++) {
			int excludePos = excludePositions[i];
			int cell = board.getCell(excludePos);
			if(board.clearFloating(excludePos, v) != cell) {
				state.addMove(Move.clearFloat(excludePos, v, "exclusion by "+note));
			}
		}
	}

	private void generateMovesForSinglePossibilities() {
		// TODO Auto-generated method stub
		
	}

	private void generateMovesForPartitioning() {
		// TODO Auto-generated method stub
		
	}

}
