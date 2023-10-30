package org.gaborsch.sudoku;

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

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return type + " " + value + ", " + Board.posToString(pos) + (note == null ? "" : " (" + note + ")");
	}
}
