package game;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Coordinates implements Serializable {
	private int row;
    private int col;
    
    public Coordinates(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Coordinates(Coordinates coordinates) {
        if (coordinates == null) return;

        this.row = coordinates.getRow();
        this.col = coordinates.getCol();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
