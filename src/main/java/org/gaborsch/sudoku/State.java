package org.gaborsch.sudoku;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class State {
	
	private Board board;
	private Queue<Move> movesLeft = new LinkedList<>();
	
	public State(Board board) {
		this.board = board;
	}
	
	public State(Board board, List<Move> initialMoves) {
		this.board = board;
		movesLeft.addAll(initialMoves);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void addMove(Move m) {
		movesLeft.add(m);
	}
	
	public Move getNextMove() {
		Move m =  movesLeft.poll();
		if (m == null) {
			m = generateMove();			
		}
		return m;
	}

	private Move generateMove() {
		return null;
	}

}
