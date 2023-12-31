package org.gaborsch.sudoku;

public class Cell {

	/*
	 * 00000000 000000FF FFFFFFFX VVVVCCCC
	 * 
	 * C: bits for possible counts, values: 1-9 V: cell value, values: 0: not fixed,
	 * 1-9: fixed X: is the value fixed? 1: yes, 0: no F: Floating (possible) values
	 */

	// not fixed, all 9 values possible
	public static final int INITIAL_VALUE = 0x0003fe09;

	private static final int MASK_COUNT = 0x0000000f;
	private static final int MASK_VALUE = 0x000000f0;
	private static final int SHIFT_VALUE = 4;
	private static final int MASK_FIXED = 0x00000100;
	private static final int SHIFT_FIXED = 8;
	private static final int[] MASK_FLOATING_BY_VALUE = new int[] { 0, 1 << 9, 1 << 10, 1 << 11, 1 << 12, 1 << 13,
			1 << 14, 1 << 15, 1 << 16, 1 << 17 };

	private static final int MASK_FLOATING = 0x0003fe00;
	private static final int SHIFT_FLOATING = 8;

	private Cell() {
	}

	public static int getCount(int bits) {
		return (bits & MASK_COUNT);
	}

	public static int getValue(int bits) {
		return (bits & MASK_VALUE) >> SHIFT_VALUE;
	}

	public static boolean isFixed(int bits) {
		return (bits & MASK_FIXED) >> SHIFT_FIXED == 1;
	}

	public static int getFloatings(int bits) {
		return (bits & MASK_FLOATING) >> SHIFT_FLOATING;
	}

	public static int getFixedAsFloatings(int bits) {
		// simulate floating bit for fixed value
		return (bits & MASK_FLOATING | MASK_FLOATING_BY_VALUE[(bits & MASK_VALUE) >> SHIFT_VALUE]) >> SHIFT_FLOATING;
	}

	public static boolean isFloating(int bits, int value) {
		return (bits & MASK_FLOATING_BY_VALUE[value]) > 0;
	}

	/**
	 * finds the cell value if only 1 possible value is present
	 * 
	 * @param bits
	 * @return
	 */
	public static int findValue(int bits) {
		if (isFixed(bits)) {
			return getValue(bits);
		}
		if (getCount(bits) > 1) {
			return 0;
		}
		for (int i = 1; i <= 9; i++) {
			if ((bits & MASK_FLOATING_BY_VALUE[i]) > 0) {
				return i;
			}
		}
		return 0;
	}

	public static int setValue(int value) {
		return MASK_FIXED | value << SHIFT_VALUE;
	}

	public static int clearFloating(int cell, int value) {
		int mask = MASK_FLOATING_BY_VALUE[value];
		if ((cell & mask) == 0) {
			return cell;
		}
		return (cell & (-1 ^ mask)) - 1;
	}

	public static int setFloating(int cell, int value) {
		int mask = MASK_FLOATING_BY_VALUE[value];
		if ((cell & mask) == mask) {
			return cell;
		}
		return (cell | mask) + 1;
	}

	public static String toString(int bits) {
		StringBuilder sb = new StringBuilder();
		if (isFixed(bits)) {
			sb.append("Fixed: ").append(getValue(bits));
		} else {
			sb.append("Floating (").append(getCount(bits)).append(") ");
			for (int i = 1; i <= 9; i++) {
				sb.append(isFloating(bits, i) ? (char)('0'+ i) : '_');
			}
		}
		sb.append("  ").append(Integer.toBinaryString(bits));
		
		return sb.toString();
	}

	public static String explain(int bits) {
		StringBuilder sb = new StringBuilder();
		if (isFixed(bits)) {
			sb.append("Fixed ");
		}
		sb.append("value: ").append(getValue(bits));
		sb.append(", Floatings: (").append(getCount(bits)).append(") ");
		for (int i = 1; i <= 9; i++) {
			sb.append(isFloating(bits, i) ? (char)('0'+ i) : '_');
		}
		sb.append("  Binary: ").append(Integer.toBinaryString(bits));
		
		return sb.toString();
	}

}
