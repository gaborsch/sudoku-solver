package org.gaborsch.sudoku;

public class Board {
	
	public static final int BOARD_SIZE = 81; 
	
	private int[] board = new int[BOARD_SIZE];

	public Board() {
		for (int i = 0; i < board.length; i++) {
			board[i] = Cell.INITIAL_VALUE;
		}
	}
	
	public Board(Board origBoard) {
		for (int i = 0; i < board.length; i++) {
			board[i] = origBoard.board[i];
		}
	}
	
	private static final int[][] BOXES = {
			{0, 1, 2, 9, 10, 11, 18, 19, 20}, 
			{3, 4, 5, 12, 13, 14, 21, 22, 23}, 
			{6, 7, 8, 15, 16, 17, 24, 25, 26}, 
			{27, 28, 29, 36, 37, 38, 45, 46, 47}, 
			{30, 31, 32, 39, 40, 41, 48, 49, 50}, 
			{33, 34, 35, 42, 43, 44, 51, 52, 53}, 
			{54, 55, 56, 63, 64, 65, 72, 73, 74}, 
			{57, 58, 59, 66, 67, 68, 75, 76, 77}, 
			{60, 61, 62, 69, 70, 71, 78, 79, 80}};
	
	private static final int[][] ROWS = {
			{0, 1, 2, 3, 4, 5, 6, 7, 8},
			{9, 10, 11, 12, 13, 14, 15, 16, 17}, 
			{18, 19, 20, 21, 22, 23, 24, 25, 26}, 
			{27, 28, 29, 30, 31, 32, 33, 34, 35}, 
			{36, 37, 38, 39, 40, 41, 42, 43, 44}, 
			{45, 46, 47, 48, 49, 50, 51, 52, 53}, 
			{54, 55, 56, 57, 58, 59, 60, 61, 62}, 
			{63, 64, 65, 66, 67, 68, 69, 70, 71}, 
			{72, 73, 74, 75, 76, 77, 78, 79, 80}};

	private static final int[][] COLS = {
			{0, 9, 18, 27, 36, 45, 54, 63, 72}, 
			{1, 10, 19, 28, 37, 46, 55, 64, 73}, 
			{2, 11, 20, 29, 38, 47, 56, 65, 74}, 
			{3, 12, 21, 30, 39, 48, 57, 66, 75}, 
			{4, 13, 22, 31, 40, 49, 58, 67, 76}, 
			{5, 14, 23, 32, 41, 50, 59, 68, 77}, 
			{6, 15, 24, 33, 42, 51, 60, 69, 78}, 
			{7, 16, 25, 34, 43, 52, 61, 70, 79}, 
			{8, 17, 26, 35, 44, 53, 62, 71, 80}};
	

	private static final int[][][] BOXES_AND_ROWS = {
			{{0, 1, 2}, {9, 10, 11}, {18, 19, 20}, {}, {}, {}, {}, {}, {}},
			{{3, 4, 5}, {12, 13, 14}, {21, 22, 23}, {}, {}, {}, {}, {}, {}}, 
			{{6, 7, 8}, {15, 16, 17}, {24, 25, 26}, {}, {}, {}, {}, {}, {}}, 
			{{}, {}, {}, {27, 28, 29}, {36, 37, 38}, {45, 46, 47}, {}, {}, {}}, 
			{{}, {}, {}, {30, 31, 32}, {39, 40, 41}, {48, 49, 50}, {}, {}, {}}, 
			{{}, {}, {}, {33, 34, 35}, {42, 43, 44}, {51, 52, 53}, {}, {}, {}}, 
			{{}, {}, {}, {}, {}, {}, {54, 55, 56}, {63, 64, 65}, {72, 73, 74}}, 
			{{}, {}, {}, {}, {}, {}, {57, 58, 59}, {66, 67, 68}, {75, 76, 77}}, 
			{{}, {}, {}, {}, {}, {}, {60, 61, 62}, {69, 70, 71}, {78, 79, 80}}};
			
	private static final int[][][] BOXES_AND_COLS = {
			{{0, 9, 18}, {1, 10, 19}, {2, 11, 20}, {}, {}, {}, {}, {}, {}}, 
			{{}, {}, {}, {3, 12, 21}, {4, 13, 22}, {5, 14, 23}, {}, {}, {}}, 
			{{}, {}, {}, {}, {}, {}, {6, 15, 24}, {7, 16, 25}, {8, 17, 26}}, 
			{{27, 36, 45}, {28, 37, 46}, {29, 38, 47}, {}, {}, {}, {}, {}, {}}, 
			{{}, {}, {}, {30, 39, 48}, {31, 40, 49}, {32, 41, 50}, {}, {}, {}}, 
			{{}, {}, {}, {}, {}, {}, {33, 42, 51}, {34, 43, 52}, {35, 44, 53}}, 
			{{54, 63, 72}, {55, 64, 73}, {56, 65, 74}, {}, {}, {}, {}, {}, {}}, 
			{{}, {}, {}, {57, 66, 75}, {58, 67, 76}, {59, 68, 77}, {}, {}, {}}, 
			{{}, {}, {}, {}, {}, {}, {60, 69, 78}, {61, 70, 79}, {62, 71, 80}}};

	
	public static int[] getRowPositions(int rowIndex) {
		return ROWS[rowIndex];
	}
	
	public static int[] getColPositions(int colIndex) {
		return COLS[colIndex];
	}

	public static int[] getBoxPositions(int boxIndex) {
		return BOXES[boxIndex];
	}

	public int[] getRowValues(int rowIndex) {
		return fetchByMapping(ROWS[rowIndex]);
	}
	
	public int[] getColValues(int colIndex) {
		return fetchByMapping(COLS[colIndex]);
	}

	public int[] getBoxValues(int boxIndex) {
		return fetchByMapping(BOXES[boxIndex]);
	}

	private int[] fetchByMapping(int[] map) {
		int[] v = new int[map.length];
		for (int i = 0; i < map.length; i++) {
			v[i] = board[map[i]];
		}
		return v;
	}
	
	public static int[] intersectBoxRow(int boxNum, int rowNum) {
		return BOXES_AND_ROWS[boxNum][rowNum];
	}
	public static int[] intersectBoxCol(int boxNum, int colNum) {
		return BOXES_AND_COLS[boxNum][colNum];
	}
	
	public static int[] subtractBoxCol(int boxNum, int colNum) {
		return subtract(BOXES[boxNum], COLS[colNum]);
	}
	public static int[] subtractBoxRow(int boxNum, int rowNum) {
		return subtract(BOXES[boxNum], ROWS[rowNum]);
	}
	public static int[] subtractColBox(int colNum, int boxNum) {
		return subtract(COLS[colNum], BOXES[boxNum]);
	}
	public static int[] subtractRowBox(int rowNum, int boxNum) {
		return subtract(ROWS[rowNum], BOXES[boxNum]);
	}

	private static int[] intersect(int[] box, int[] line) {
		// box and line have 3 matches
		int[] ints = new int[3];
		int cnt = 0;
		for (int i = 0; i < box.length; i++) {
			for (int j = 0; j < line.length; j++) {
				if(box[i] == line[j]) {
					ints[cnt++] = box[i];
				}
			}
		}		
		return ints;
	}
	
	private static int[] subtract(int[] box, int[] line) {
		// box and line have 6 non-matches
		int[] ints = new int[6];
		int cnt = 0;
		for (int i = 0; i < box.length; i++) {
			boolean found = false;
			for (int j = 0; j < line.length; j++) {
				if(box[i] == line[j]) {
					found = true;
					break;
				}
			}
			if (!found) {
				ints[cnt++] = box[i];
			}
		}		
		return ints;
	}
	
	/*
	 * counts how many times the given floating value exists at the given positions 
	 */
	public int countFloatingValue(int value, int[] positions) {
		int count = 0;
		for (int i = 0; i < positions.length; i++) {
			if(Cell.isFloating(board[positions[i]], value)) {
				count++;
			}
		}
		return count;
	}
	
	/*
	 * returns the first position of a floating value using position array
	 */
	public int getFirstFloatingValuePosition(int value, int[] positions) {
		for (int i = 0; i < positions.length; i++) {
			if(Cell.isFloating(board[positions[i]], value)) {
				return positions[i];
			}
		}
		return -1;
	}
	
	public void setFixedValue(int pos, int value) {
		board[pos] = Cell.setValue(value);
	}

	public int getCell(int pos) {
		return board[pos];
	}
	
	public void setCell(int pos, int cell) {
		board[pos] = cell;
	}
	
	public int clearFloating(int pos, int value) {
		board[pos] = Cell.clearFloating(board[pos], value);
		return board[pos];
	}
	
	public static int getRowNum(int pos) {
		return pos / 9;
	}
	
	public static int getColNum(int pos) {
		return pos % 9;
	}
	
	public static int getBoxNum(int pos) {
		return (pos / 27) * 3 + (pos % 9) / 3;
	}

	public String draw() {
		return new BoardDrawer().draw();
	}
	
	public class BoardDrawer {
		StringBuilder sb = new StringBuilder();
		
		public String draw() {
			drawSeparator(0);
			for (int i = 0; i < 9; i++) {
				int[] nums = getRowValues(i);
				drawLine(nums, 0);
				drawLine(nums, 1);
				drawLine(nums, 2);
				drawSeparator(i+1);
			}
			drawSummary();
			return sb.toString();
		}
		
		private void drawLine(int[] nums, int line) {
			for (int j = 0; j < nums.length; j++) {
				if (j%3==0) {
					sb.append("|");
				}
				drawCell(nums[j], line);
				sb.append("|");
			}
			drawNl();
		}

		private void drawCell(int cell, int line) {
			if(Cell.isFixed(cell)) {
				switch (line) {
				case 0: 
					sb.append("/=\\");
					break;
				case 1: 
					sb.append(" " + Cell.getValue(cell) + " ");
					break;
				case 2: 
					sb.append("\\=/");
					break;
				}
			} else {
				for (int i = 0; i < 3; i++) {
					int value = line * 3 + i+1;
					sb.append((char) (Cell.isFloating(cell, value) ? '0'+value : ' '));
				}
			}
		}

		private void drawSeparator(int row) {
			if(row%3==0) {
				sb.append("+===+===+===+".repeat(3));
			} else {
				sb.append("+---+---+---+".repeat(3));
			}
			drawNl();
		}
		
		private void drawSummary() {
			int[] counts = new int[] {0,0,0,0,0,0,0,0,0,0};
			for (int i = 0; i < board.length; i++) {
				if (Cell.isFixed(board[i])) {
					counts[Cell.getValue(board[i])]++;
				}
			}
			for (int i = 1; i < counts.length; i++) {
				sb.append(i).append(":").append(9-counts[i]).append("  ");
			}
			drawNl();
		}

		private void drawNl() {
			sb.append("\n");
		}

	}
	
	@Override
	public int hashCode() {
		int h = 23;
		for (int i = 0; i < board.length; i++) {
			h = h*23+board[i];
		}
		return h;
	}
}