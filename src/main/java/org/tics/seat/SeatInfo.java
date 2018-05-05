package org.tics.seat;

/**
 * Denotes a particular seat. Holds the row and seat position in the row.
 */
public class SeatInfo {

    int row;
    int col;

    public SeatInfo(int row, int col) {
       this.row = row;
       this.col = col;
    }

    public int getRow() {
       return row;
    }

    public int getCol() {
       return col;
    }

    @Override
    public String toString() {
        return "SeatInfo{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }
}