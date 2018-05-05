package org.tics.ticket;

import org.junit.Ignore;
import org.junit.Test;
import org.tics.seat.SeatHold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class TicketServiceImplMultiuserTest {

    @Test
    public void testReserveTicketsConcurrent() throws Exception {
        int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M);
        assertEquals(N*M, impl.numSeatsAvailable());

        int threadCnt = 2;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCnt);
        List<Future> futures = new ArrayList<>();
        List<SeatHold> holds = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger reqCnt = new AtomicInteger(0);
        int numPerRequest = 2;

        for(int j=0;j<threadCnt;j++) {
            futures.add(executor.submit(() -> {
                for(int i=0;i<N*2;i++) {
                    SeatHold hold = impl.findAndHoldSeats(numPerRequest, "a@a.com");
                    assertNotNull(hold);
                    assertNotNull(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
                    reqCnt.updateAndGet(x -> x + numPerRequest);
                    holds.add(hold);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int holdCount = holds.stream().mapToInt( h -> h!= null ? h.getNumSeats() :0).sum();
        assertEquals(reqCnt.get(), holdCount);
        assertEquals(N * M - reqCnt.get(), impl.numSeatsAvailable());
    }


    @Test
    public void testReserveTicketsConcurrentRandom() throws Exception {
        int N = 20, M = 50;
        TicketServiceImpl impl = new TicketServiceImpl(N, M);
        assertEquals(N*M, impl.numSeatsAvailable());

        int threadCnt = 10;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCnt);
        List<Future> futures = new ArrayList<>();
        List<SeatHold> holds = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger reqCnt = new AtomicInteger();
        for(int j=0;j<threadCnt;j++) {
            futures.add(executor.submit(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for(int i=0;i<N*2;i++) {
                    int curRqCnt = random.nextInt(1,5);
                    assertTrue(curRqCnt > 0 && curRqCnt <= 5);
                    SeatHold hold = impl.findAndHoldSeats(curRqCnt, "a@a.com");
                    assertNotNull(hold);
                    if(curRqCnt % 2 == 0) {
                        assertNotNull(impl.reserveSeats(hold.getHoldId(), "a@a.com"));
                        reqCnt.updateAndGet( x -> x + curRqCnt);
                        holds.add(hold);
                    }
                }
            }));
        }
        try {
            Thread.sleep(15* 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while(executor.getCompletedTaskCount() != threadCnt);
        assertEquals(N * M - reqCnt.get(), impl.numSeatsAvailable());
        int holdCount = holds.stream().mapToInt( h -> h!= null ? h.getNumSeats() :0).sum();
        assertEquals(reqCnt.get(), holdCount);
    }

    @Ignore
    @Test
    public void testReserveTicketsConcurrentRandomNTimes() throws Exception {
        for(int i=0;i<10;i++) {
            try {
                System.out.println("Run - "+i);
                testReserveTicketsConcurrentRandom();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


}