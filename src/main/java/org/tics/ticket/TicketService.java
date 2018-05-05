package org.tics.ticket;

import org.tics.seat.SeatHold;

public interface TicketService {

    int numSeatsAvailable();
    /**
     * Find and hold the best available seats for a customer
     *
     * @param numSeats the number of seats to find and hold
     * @param customerEmail unique identifier for the customer
     * @return a SeatHold object identifying the specific seats and related information
     */
    SeatHold findAndHoldSeats(int numSeats, String customerEmail);
    /**
     * Commit seats held for a specific customer
     *
     * @param seatHoldId the ticket hold identifier
     * @param customerEmail the email address of the customer to which the ticket hold is assigned
     * @return a reservation confirmation code
     */
    String reserveSeats(int seatHoldId, String customerEmail);

}