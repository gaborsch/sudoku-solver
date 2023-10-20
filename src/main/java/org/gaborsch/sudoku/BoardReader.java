package org.gaborsch.sudoku;

import java.util.ArrayList;
import java.util.List;

public class BoardReader {

	public static List<Move> getBoard(String s) {
		List<Move> moves = new ArrayList<>();
		int i = 0;
		for (String line : s.split("\n")) {
			line = line.concat("         ");
			for (int j = 0; j < 9; j++) {
				char c = line.charAt(j);
				if (c >= '1' && c <= '9') {
					moves.add(Move.setValue(i * 9 + j, c - '0', "initial"));
				}
			}
			i++;
		}
		return moves;
	}

}
