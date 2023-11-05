package org.gaborsch.sudoku;

public class MoveGenerator {

	private State state;
	private Board board;

	public MoveGenerator(State state) {
		this.state = state;
		this.board = state.getBoard();
	}

	public void generateMoves() {
		generateMovesForSinglePossibilities();
		if (!state.hasNextMove()) {
			generateMovesForRowColExclusion();
		}
		if (!state.hasNextMove()) {
			excludeByComplementers();
		}
		if (!state.hasNextMove()) {
			generateMovesForPartitioning();
		}

	}

	
	private void generateMovesForSinglePossibilities() {
		for (int v = 1; v <= 9; v++) {
			for (int rn = 0; rn < 9; rn++) {
				checkSingleValue(v, Board.getRowPositions(rn), "single value in row " + (rn + 1));
			}
			for (int cn = 0; cn < 9; cn++) {
				checkSingleValue(v, Board.getColPositions(cn), "single value in column " + (cn + 1));
			}
			for (int bn = 0; bn < 9; bn++) {
				checkSingleValue(v, Board.getBoxPositions(bn), "single value in box " + (bn + 1));
			}
		}
	}

	/*
	 * checks if value v is a floating value at a single position if the position is
	 * found, marks as a fixed value
	 */
	private void checkSingleValue(int v, int[] positions, String note) {
		if (board.countFloatingValue(v, positions) == 1) {
			int pos = board.getFirstFloatingValuePosition(v, positions);
			setValue(v, pos, note);
		}
	}

	
	private static final int FIXED_MARKER_IN_FLOATINGS = 1 << 16;

	private void generateMovesForRowColExclusion() {
		int[][] floatingsByRowAndBox = VectorCache.floatingsByRowAndBoxMatrix();
		int[][] floatingsByColAndBox = VectorCache.floatingsByColAndBoxMatrix();
		for (int pos = 0; pos < Board.BOARD_SIZE; pos++) {
			int floatings = Cell.getFloatings(board.getCell(pos));
			int fixed = Cell.isFixed(board.getCell(pos)) ? FIXED_MARKER_IN_FLOATINGS : 0;
			floatingsByRowAndBox[Board.getRowNum(pos)][Board.getBoxNum(pos)] |= floatings | fixed;
			floatingsByColAndBox[Board.getColNum(pos)][Board.getBoxNum(pos)] |= floatings | fixed;
		}

		// check each value
		for (int v = 1; v <= 9; v++) {
			// check rows
			for (int rn = 0; rn < 9; rn += 3) {
				int excludeRowBoxPairs = checkExclusion(v, floatingsByRowAndBox[rn], floatingsByRowAndBox[rn + 1],
						floatingsByRowAndBox[rn + 2]);
				while ((excludeRowBoxPairs & 0xff) != 0xff) {
					int excludeRn = (int) (rn + ((excludeRowBoxPairs & 0x30) >> 4));
					int excludeBn = (int) (excludeRowBoxPairs & 0xf);
					excludeRowBoxPairs >>= 6;
					// exclude value 'v' from row and box
					int[] excludePositions = Board.intersectBoxRow(excludeBn, excludeRn);
					excludeFloatings(v, excludePositions, "box " + (excludeBn + 1) + " and row " + (excludeRn + 1));
				}

			}
			// check cols
			for (int cn = 0; cn < 9; cn += 3) {
				int excludeColBoxPairs = checkExclusion(v, floatingsByColAndBox[cn], floatingsByColAndBox[cn + 1],
						floatingsByColAndBox[cn + 2]);
				while ((excludeColBoxPairs & 0xff) != 0xff) {
					int excludeCn = (int) (cn + ((excludeColBoxPairs & 0x30) >> 4));
					int excludeBn = (int) (excludeColBoxPairs & 0xf);
					excludeColBoxPairs >>= 6;
					// exclude value 'v' from row and box
					int[] excludePositions = Board.intersectBoxCol(excludeBn, excludeCn);
					excludeFloatings(v, excludePositions, "box " + (excludeBn + 1) + " and col " + (excludeCn + 1));
				}
			}
		}
	}

	private int checkExclusion(int v, int[] s0, int[] s1, int[] s2) {
		int retval = 0xff;
		// find out possible boxes (always 3)
		int[] b = VectorCache.checkExclusionVector();
		for (int bn = 0, bi = 0; bi < 3; bn++) {
			if (s0[bn] + s1[bn] + s2[bn] > 0) {
				b[bi++] = bn;
			}
		}

		// iterate through boxes
		for (int bi = 0; bi < b.length; bi++) {
			int bn = b[bi];
			// boxes in this series
			// count of series that may contain v
			int s0n = (s0[bn] >> v) & 1;
			int s1n = (s1[bn] >> v) & 1;
			int s2n = (s2[bn] >> v) & 1;
			// does it only appears in 1 series in the box?
			if (s0n + s1n + s2n == 1) {
				// then exclude other boxes in that series
				int sn = s1n + 2 * s2n;
				retval = markExclude(retval, sn, b[0], bn != b[0]);
				retval = markExclude(retval, sn, b[1], bn != b[1]);
				retval = markExclude(retval, sn, b[2], bn != b[2]);
			}
		}

		return retval;
	}

	private final int markExclude(int retval, int sn, int bn, boolean condition) {
		return condition ? (retval << 6) | (sn << 4) | bn : retval;
	}

	private void excludeByComplementers() {
		for (int rn = 0; rn < 9; rn++) {
			excludeByRowComplementers(rn);
		}
		for (int cn = 0; cn < 9; cn++) {
			excludeByColComplementers(cn);
		}
	}

	int[][] COMPLEMENTER_ROWCOLNUMS = {{1,2},{0,2},{0,1},{4,5},{3,5},{3,4},{7,8},{6,8},{6,7}};  
	
	private void excludeByRowComplementers(int rn) {
		int[] otherRns = COMPLEMENTER_ROWCOLNUMS[rn];
		int[] row = Board.getRowPositions(rn);
		int[] row2 = Board.getRowPositions(otherRns[0]);
		int[] row3 = Board.getRowPositions(otherRns[1]);
		for (int i = 0; i < 3; i++) {
			int bn = ((rn / 3) * 3) + i;
			// find row-complementer exclusions for row rn and box bn
			excludeByComplementers(bn, row, row2, row3,
					"exclude by complementers of row " + (rn+1) + " in box " + (bn+1));
		}
	}

	private void excludeByColComplementers(int cn) {
		int[] otherCns = COMPLEMENTER_ROWCOLNUMS[cn];
		int[] col = Board.getColPositions(cn);
		int[] col2 = Board.getColPositions(otherCns[0]);
		int[] col3 = Board.getColPositions(otherCns[1]);
		for (int i = 0; i < 3; i++) {
			int bn = (cn / 3) + i * 3;
			// find column-complementer exclusions for column cn and box bn
			excludeByComplementers(bn, col, col2, col3,
					"complementers of col " + (cn+1) + " in box " + (bn+1));
		}
	}
	
	private void excludeByComplementers(int bn, int[] series, int[] series2, int[] series3, String note) {
		int insideBox = 0;
		int outsideBox = 0;
		// calculate possible values for the series, separately for inside and outside of the box 
		for (int i = 0; i < series.length; i++) {
			if (Board.getBoxNum(series[i]) == bn) {
				// we can only exclude floatings
				insideBox |= Cell.getFloatings(board.getCell(series[i]));
			} else {
				outsideBox |= Cell.getFixedAsFloatings(board.getCell(series[i]));
			}
		}
		// check if there are values inside that are not outside
		int valuesToExclude = insideBox ^ (insideBox & outsideBox);
		if (valuesToExclude != 0) {
			for (int v = 1; v<=9 && valuesToExclude > 0; v++) {
				if ((valuesToExclude & (1<<v)) > 0) {
					valuesToExclude ^= 1<<v;
					// exclude value v from series 2 and 3 intersecting box bn
					for (int i = 0; i < 9; i++) {
						if (Board.getBoxNum(series2[i]) == bn) {
							excludeFloating(v, series2[i], note);
						}
						if (Board.getBoxNum(series3[i]) == bn) {
							excludeFloating(v, series3[i], note);
						}
					}
				}
			}
		}
		
	}

	private void generateMovesForPartitioning() {
		for (int rn = 0; rn < 9; rn++) {
			partition(Board.getRowPositions(rn), "partition of row " + (rn + 1));
		}
		for (int cn = 0; cn < 9; cn++) {
			partition(Board.getColPositions(cn), "partition of col " + (cn + 1));
		}
		for (int bn = 0; bn < 9; bn++) {
			partition(Board.getBoxPositions(bn), "partition of box " + (bn + 1));
		}
	}

	private void partition(int[] positions, String note) {
		int[] floatings = VectorCache.partitionFloatingsVector(positions.length);
		// extract floating bits (9 bits, offset 1-9)
		int variantMask = 0;
		for (int i = 0; i < positions.length; i++) {
			floatings[i] = Cell.getFloatings(board.getCell(positions[i]));
			variantMask |= (floatings[i] > 0) ? 1 << i : 0;
		}
		int inverseMask = ~variantMask; // -variantMask - 1;

		// iterate through variants, all clear and all set excluded
		for (int variant = 1; variant < 511; variant++) {
			// if it has non-valid bits or has no valid bits or is full mask, then continue
			if ((variant & inverseMask) > 0 || (variant & variantMask) == 0 || (variant & variantMask) == variantMask) {
				continue;
			}

			int u = unionFloatingsOfVariant(floatings, variant);
			// if n values fit n places, these values should not appear elsewhere
			if (Integer.bitCount(u) == Integer.bitCount(variant & variantMask)) {
				// clear all positions that are in variant
				int[] positionsCleared = VectorCache.partitionPositionsClearedVector(positions.length);
				for (int i = 0; i < positions.length; i++) {
					positionsCleared[i] = ((variant & (1 << i)) > 0) ? -1 : positions[i];
				}
				// the remaining positions should be cleared
				// clear values present in 'u' from position not covered in 'variant'
				for (int v = 1; v <= 9; v++) {
					// if value v is set in u
					if ((u & (1 << v)) > 0) {
						excludeFloatings(v, positionsCleared, note);
					}
				}
				return;
			}
		}

	}

	private int unionFloatingsOfVariant(int[] floatings, int variant) {
		int u = 0;
		for (int i = 0; i < 9; i++) {
			if (((1 << i) & variant) > 0) {
				u |= floatings[i];
			}
		}
		return u;
	}

	/*
	 * Exclude value 'v' from given positions, mark clearing if needed. 
	 * Negative positions are skipped
	 */
	private void excludeFloatings(int v, int[] excludePositions, String note) {
		for (int i = 0; i < excludePositions.length; i++) {
			excludeFloating(v, excludePositions[i], note);
		}
	}

	/*
	 * Exclude value 'v' from given position, mark clearing if needed.
	 * Negative positions are skipped
	 */
	private void excludeFloating(int v, int excludePos, String note) {
		if (excludePos >= 0) {
			int cell = board.getCell(excludePos);
			if (board.clearFloating(excludePos, v) != cell) {
				state.addMove(Move.clearFloat(excludePos, v, "exclusion by " + note));
			}
		}
	}

	/*
	 * Set value 'v' from given positions, mark setting if needed
	 */
	private void setValue(int v, int position, String note) {
		int cell = board.getCell(position);
		if (board.setFixedValue(position, v) != cell) {
			state.addMove(Move.setValue(position, v, "value set by " + note));
		}
	}

}
