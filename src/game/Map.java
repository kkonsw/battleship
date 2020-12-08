package game;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Map implements Serializable {
	private Field[][] grid;
	private static final int numRows = 10;
	private static final int numColumns = 10;
	
	public Map() {
		grid = new Field[numRows][numColumns];
		
		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numColumns; j++) {
				grid[i][j] = Field.EMPTY;
			}		
	}
	
	public Map(Map otherMap) {
		grid = new Field[numRows][numColumns];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = otherMap.getField(i, j);
            }
        }
    }
	
	public Field getField(int row, int column) {
		return grid[row][column];
	}
	
	public void setField(int row, int column, Field newField) {
		grid[row][column] = newField;
	}
	
    public int countFields(Field fieldState) {
        int result = 0;
        for (Field[] fsRow : grid) {
            for (Field fs : fsRow) {
                if (fs == fieldState)
                    ++result;
            }
        }

        return result;
    }
	
	public int updateMap(Coordinates coordinates) {
		int row = coordinates.getRow();
        int col = coordinates.getCol();

        Field fs = getField(row, col);
        if (fs == Field.EMPTY) {
            setField(row, col, Field.EMPTY_SHOT);
            return 0;
        }

        if (fs == Field.SHIP) {
            setField(row, col, Field.SHIP_HIT);
            return 1;
        }

        return -1;
	}
}
