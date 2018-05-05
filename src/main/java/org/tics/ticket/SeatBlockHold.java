package org.tics.ticket;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Structure for internal seat block hold for a request. This differs from the SeatHold structure as it is used to
 * hold the block of seats and is internal in nature.
 */
public class SeatBlockHold {
    private int holdId;
    private List<SeatBlock> seatBlocks;
    private Instant expirationTime;

    public SeatBlockHold() {
    }

    public SeatBlockHold(int holdId) {
        this.holdId = holdId;
    }

    public SeatBlockHold(int holdId, List<SeatBlock> seatBlocks, Instant expirationTime) {
        this.holdId = holdId;
        this.expirationTime = expirationTime;
        this.seatBlocks = seatBlocks;
    }


    @Override
    public boolean equals(Object o) {
        return Objects.equals(holdId, ((SeatBlockHold)o).holdId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(holdId);
    }

    public void setHoldId(int holdId) {
        this.holdId = holdId;
    }

    public void setSeatBlocks(List<SeatBlock> seatBlocks) {
        this.seatBlocks = seatBlocks;
    }

    public int getHoldId() {
        return holdId;
    }

    public List<SeatBlock> getSeatBlocks() {
        return seatBlocks;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

}
