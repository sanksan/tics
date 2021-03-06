package org.tics.ticket;

/**
 * Structure an available or allocated seat block for a customer
 */
public class SeatBlock {

    int row;
    int col;
    int length;

    public SeatBlock(int row, int col) {
       this.row = row;
       this.col = col;
    }

    public int getRow() {
       return row;
    }

    public int getLength() {
       return length;
    }

    public void setLength(int length) {
       this.length = length;
    }

    public int getCol() {
       return col;
    }


}