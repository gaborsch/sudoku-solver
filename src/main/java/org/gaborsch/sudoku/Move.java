package org.gaborsch.sudoku;

import java.util.Objects;

public class Move {

	enum Type {
		SET_VALUE, CLEAR_FLOAT
	};

	private Type type;
	private int pos;
	private int value;
	private String note;

	public static Move setValue(int pos, int value, String note) {
		return new Move(Type.SET_VALUE, pos, value, note);
	}

	public static Move clearFloat(int pos, int value, String note) {
		return new Move(Type.CLEAR_FLOAT, pos, value, note);
	}

	public Move(Type type, int pos, int value, String note) {
		this.type = type;
		this.pos = pos;
		this.value = value;
		this.note = note;
	}

	public Type getType() {
		return type;
	}

	public int getPos() {
		return pos;
	}

	public int getRowNum() {
		return Board.getRowNum(pos);
	}
	
	public int getColNum() {
		return Board.getColNum(pos);
	}
	
	public int getBoxNum() {
		return Board.getBoxNum(pos);
	}
	
	public int getValue() {
		return value;
	}
	
	public String getCoords() {
		return getRowCoord() + ", " +getColCoord();
	}
	
	public String getRowCoord() {
		return "row " + (Board.getRowNum(pos)+1);
	}
	
	public String getColCoord() {
		return "col " + (Board.getColNum(pos)+1);
	}

	public String getBoxCoord() {
		return "box " + (Board.getBoxNum(pos)+1);
	}
	
	
	@Override
	public String toString() {
		return type + " " + value + ", " + Board.posToString(pos) + (note == null ? "" : " (" + note + ")");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Move) {
			Move other = (Move) obj;
			return Objects.equals(type, other.type)
					&& Objects.equals(pos, other.pos)
					&& Objects.equals(value, other.value);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, pos, value);
	}
}
