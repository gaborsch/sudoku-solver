package org.gaborsch.sudoku;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class State {

	private Board board;
	private Queue<Move> setValueMoves = new LinkedList<>();
	private Queue<Move> clearFloatMoves = new LinkedList<>();

	public State(Board board) {
		this.board = board;
	}

	public State(Board board, List<Move> initialMoves) {
		this.board = board;
		initialMoves.forEach(this::addMove);
	}

	public Board getBoard() {
		return board;
	}

	public void addMove(Move m) {
		switch (m.getType()) {
		case SET_VALUE:
			setValueMoves.add(m);
			break;
		case CLEAR_FLOAT:
			clearFloatMoves.add(m);
			break;
		}
	}

	public boolean hasNextMove() {
		return !setValueMoves.isEmpty() || !clearFloatMoves.isEmpty();
	}

	public Move getNextMove() {
		Move m = setValueMoves.poll();
		if (m == null) {
			m = clearFloatMoves.poll();
		}
		return m;
	}

}
