package org.tics.ticket;

import org.junit.Ignore;
import org.junit.Test;
import org.tics.seat.SeatHold;

import static org.junit.Assert.*;

public class TicketServiceImplTest {

    @Test
    public void testHoldAndReserveSeats() throws Exception {
        int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M, 1*1000);
        assertEquals(N*M, impl.numSeatsAvailable());
        SeatHold hold = impl.findAndHoldSeats(2, "a@a.com");
        assertEquals(N*M-2, impl.numSeatsAvailable());
        assertNotNull(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
//        System.out.println(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
        assertEquals(N*M-2, impl.numSeatsAvailable());
    }

    @Test
    public void testReserveSeatsWithDuplicateHoldId() throws Exception {
        int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M, 1*1000);
        assertEquals(N*M, impl.numSeatsAvailable());
        SeatHold hold = impl.findAndHoldSeats(2, "a@a.com");
        assertEquals(N*M-2, impl.numSeatsAvailable());
        assertNotNull(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
        assertEquals(N*M-2, impl.numSeatsAvailable());
        assertNull(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSeatConfig_Rows() throws Exception {
        new TicketServiceImpl(-1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSeatConfig_SeatsInRow() throws Exception {
        new TicketServiceImpl(10, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSeatConfig_ZeroRows() throws Exception {
        new TicketServiceImpl(0, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSeatConfig_ZeroSeatsInRow() throws Exception {
        new TicketServiceImpl(10, 0);
    }

    @Test
    public void findAndHoldFewBatches() throws Exception {
        int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M, 1*1000);
        assertEquals(N*M, impl.numSeatsAvailable());
        for(int i=0;i<N*2;i++) {
            SeatHold hold = impl.findAndHoldSeats(2, "a@a.com");
            assertEquals(N * M - (i+1)*2, impl.numSeatsAvailable());
            assertNotNull(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
            assertEquals(N * M - (i+1)*2, impl.numSeatsAvailable());
        }
        SeatHold hold = impl.findAndHoldSeats(2, "a@a.com");
        assertNotNull(hold);
        assertEquals(0, impl.numSeatsAvailable());
        hold = impl.findAndHoldSeats(1, "a@a.com");
        assertNotNull(hold.getErrorInfo());
        assertEquals(0, impl.numSeatsAvailable());
    }

    @Test
    public void findAndHoldSeatsExpired() throws Exception {
        int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M, 1*1000);
        assertEquals(N*M, impl.numSeatsAvailable());
        SeatHold hold = impl.findAndHoldSeats(2, "a@a.com");
        assertEquals(N*M-2, impl.numSeatsAvailable());
        Thread.sleep(3*1000);
        assertNull(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
        assertEquals(N*M, impl.numSeatsAvailable());
    }

    @Test
    public void findAndHoldSeatsOverTheLimit() throws Exception {
        int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M, 1*1000);
        assertEquals(N*M, impl.numSeatsAvailable());
        SeatHold hold = impl.findAndHoldSeats(20, "a@a.com");
        assertNotNull(hold.getErrorInfo());
        assertEquals(N*M, impl.numSeatsAvailable());
    }

    @Test
    public void reserveWithNullHoldId() throws Exception {
        int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M, 1*1000);
        assertNull(impl.reserveSeats(-1, "a@a.com"));
        assertEquals(N*M, impl.numSeatsAvailable());
    }

}