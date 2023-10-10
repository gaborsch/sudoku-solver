package org.gaborsch.sudoku;

public class Move {
	
	enum Type { SET_VALUE, CLEAR_FLOAT };
	
	private Type type;
	private int pos;
	private int value;
	
	public static Move setValue(int pos, int value) {
		return new Move(Type.SET_VALUE, pos, value);
	}
	
	public static Move clearFloat(int pos, int value) {
		return new Move(Type.CLEAR_FLOAT, pos, value);
	}
	
	public Move(Type type, int pos, int value) {
		this.type = type;
		this.pos = pos;
		this.value = value;
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
		return type + " "+ value+ " @ " + pos;
	}
}
