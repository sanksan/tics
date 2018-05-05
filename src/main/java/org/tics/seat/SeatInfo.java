package org.tics.seat;

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