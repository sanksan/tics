package org.tics.seat;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * DTO for the result of a reservation hold request.
 */
public class SeatHold {
    private int holdId;
    private int numSeats;
    private List<SeatInfo> seats;
    private String customerEmail;
    private Instant expirationTime;
    private ErrorInfo errorInfo;

    public SeatHold() {
    }

    public SeatHold(int holdId, String customerEmail, int numSeats, List<SeatInfo> seats, Instant expirationTime) {
        this.holdId = holdId;
        this.numSeats = numSeats;
        this.customerEmail = customerEmail;
        this.expirationTime = expirationTime;
        this.seats = seats;
    }

    public SeatHold(String customerEmail, int numSeats, ErrorInfo errorInfo) {
        this.customerEmail = customerEmail;
        this.seats = seats;
        this.errorInfo = errorInfo;
    }

    public ErrorInfo getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(ErrorInfo errorInfo) {
        this.errorInfo = errorInfo;
    }


    @Override
    public boolean equals(Object o) {
        return Objects.equals(holdId, ((SeatHold)o).holdId);
    }

    @Override
    public String toString() {
        return "SeatHold{" +
                "holdId=" + holdId +
                ", numSeats=" + numSeats +
                ", seats=" + seats +
                ", customerEmail='" + customerEmail + '\'' +
                ", expirationTime=" + expirationTime +
                ", errorInfo=" + errorInfo +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(holdId);
    }

    public void setHoldId(int holdId) {
        this.holdId = holdId;
    }

    public void setNumSeats(int numSeats) {
        this.numSeats = numSeats;
    }

    public void setSeats(List<SeatInfo> seats) {
        this.seats = seats;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setExpirationTime(Instant expirationTime) {
        this.expirationTime = expirationTime;
    }

    public int getHoldId() {
        return holdId;
    }

    public int getNumSeats() {
        return numSeats;
    }

    public List<SeatInfo> getSeats() {
        return seats;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }

}
