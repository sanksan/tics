package org.tics.ticket;

import org.tics.seat.*;
import org.tics.util.HoldIdGenerator;
import org.tics.util.ReservationIdGenerator;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class TicketServiceImpl implements TicketService {

    private Timer cleanExpHoldTimer;
    private int holdPeriod;//milliseconds

    private AtomicInteger freeSeats;
    private PriorityBlockingQueue<SeatBlockHold> holdQueue;
    private Map<Integer, List<SeatBlock>> availMap;

    private Map<String, SeatBlockHold> reservedSeats = new HashMap<>();

    private HoldIdGenerator holdIdGenerator;
    private ReservationIdGenerator resIdGenerator;

    public TicketServiceImpl(int rows, int seatsInRow) {
        this(rows, seatsInRow, 1 * 1000);
    }

    public TicketServiceImpl(int rows, int seatsInRow, int holdPeriod) {

        if(rows <= 0 || seatsInRow <= 0 || holdPeriod <= 0)
            throw new IllegalArgumentException("rows/seatsInRow has to be valid non-zero number");

        this.holdIdGenerator = new HoldIdGenerator();
        this.resIdGenerator = new ReservationIdGenerator();

        this.reservedSeats = new ConcurrentHashMap<>();
        this.availMap = new ConcurrentSkipListMap<>();

        this.holdQueue = new PriorityBlockingQueue<>(rows, (o1, o2) ->
                                                                (int)(o1.getExpirationTime().toEpochMilli() -
                                                                        (o2.getExpirationTime().toEpochMilli())));
        List<SeatBlock> seatBlocks = new CopyOnWriteArrayList<>();
        for(int i=1;i<=rows;i++) {
            seatBlocks.add(new SeatBlock(i, 1));
        }
        this.availMap.put(seatsInRow, seatBlocks);
        this.freeSeats = new AtomicInteger(rows * seatsInRow);

        this.holdPeriod = holdPeriod;
        scheduleCleanExpiredHolds();
    }

    public void turnOffSchedule() {
        if(cleanExpHoldTimer != null)
            cleanExpHoldTimer.cancel();
    }

    public void scheduleCleanExpiredHolds() {
        System.out.println(String.format("scheduling cleanExpHoldTimer every %s milliseconds to clean up expired holds", holdPeriod));
        cleanExpHoldTimer = new Timer();
        cleanExpHoldTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cleanExpiredHolds();
            }
        }, 0, holdPeriod);
    }

    void cleanExpiredHolds() {
//        System.out.println("cleanExpiredHolds() - "+ (!holdQueue.isEmpty() ? holdQueue.peek().getExpirationTime() : "") + " "+(Instant.now()));
        while(!holdQueue.isEmpty() && holdQueue.peek().getExpirationTime().isBefore(Instant.now())) {
            SeatBlockHold hold = holdQueue.poll();
            releaseHold(hold.getSeatBlocks());
        }
    }

    void releaseHold(List<SeatBlock> seatBlocks) {
        for(SeatBlock seatBlock : seatBlocks) {
            availMap.putIfAbsent(seatBlock.getLength(), new CopyOnWriteArrayList<>());
            availMap.get(seatBlock.getLength()).add(new SeatBlock(seatBlock.getRow(), seatBlock.getCol()));
            freeSeats.updateAndGet((x) -> x + seatBlock.getLength());
        }
    }

    @Override
    public int numSeatsAvailable() {
        return freeSeats.intValue();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        if(numSeats <= 0 || customerEmail != null || customerEmail.isEmpty())
            return new SeatHold(customerEmail,
                    numSeats,
                    new ErrorInfo("INVALID_INPUT", "The numSeats/customerEmail is invalid. Please check and try again"));

        SeatHold hold = null;
        SeatBlock seatBlock = null;
        List<SeatBlock> seatBlocks = new ArrayList<>();

        //find seats from available pool.
        //1. find a block with same size as requested number of seats
        //2. if not found, chop a slice of next higher block
        //3. if not found, split requested number of seats into small number of blocks
        List<SeatBlock> list = availMap.get(numSeats);
        if(list == null || list.isEmpty()) {
            //Couldn't find a block size same as requested; searching for next higher seat block
            Map.Entry<Integer, List<SeatBlock>> seatEntry = null;
            int k =  numSeats;
            do {
                seatEntry = ((ConcurrentSkipListMap) availMap).higherEntry(k++);
                if(seatEntry == null)
                    break;
                list = seatEntry.getValue();
                if (list != null && !list.isEmpty()) {
                    SeatBlock removedBlock = list.remove(0);
                    availMap.putIfAbsent(seatEntry.getKey() - numSeats, new CopyOnWriteArrayList<>());
                    availMap.get(seatEntry.getKey() - numSeats).add(new SeatBlock(removedBlock.getRow(), removedBlock.getCol() + numSeats));
                    seatBlock = removedBlock;
                    seatBlock.setLength(numSeats);
                    seatBlocks.add(seatBlock);
                    break;
                }
            } while(seatEntry != null);
            //couldn't find a single contiguous block of seats
            //split the requested num and find seatBlocks
            if(seatEntry == null) {
                int numRem = numSeats;
                k = numRem;
                do {
                    seatEntry = ((ConcurrentSkipListMap) availMap).lowerEntry(k--);
                    if(seatEntry == null)
                        break;
                    list = seatEntry.getValue();
                    if (list != null) {
                        while(!list.isEmpty() && numRem > 0) {
                            SeatBlock removedBlock = list.remove(0);
                            if(numRem < seatEntry.getKey()) {
                                availMap.putIfAbsent(numRem - seatEntry.getKey(), new CopyOnWriteArrayList<>());
                                availMap.get(numRem - seatEntry.getKey()).add(new SeatBlock(removedBlock.getRow(), removedBlock.getCol() + numRem));
                                seatBlock = removedBlock;
                                seatBlock.setLength(seatEntry.getKey());
                                seatBlocks.add(seatBlock);
                            } else {
                                removedBlock.setLength(seatEntry.getKey());
                                seatBlocks.add(removedBlock);
                            }
                            numRem -= seatEntry.getKey();
                        }
                    }
                } while(seatEntry != null && numRem > 0);
            }
        } else {
            seatBlock = list.remove(0);
            seatBlock.setLength(numSeats);
            seatBlocks.add(seatBlock);
        }

        if(seatBlocks != null && !seatBlocks.isEmpty()) {
            List<SeatInfo> seatInfo = new LinkedList<>();
            int k = 0;
            //populate seat info based on allocated seat blocks
            for(SeatBlock tmpSeatBlock : seatBlocks) {
                for(int i = 0; i< tmpSeatBlock.getLength(); i++) {
                    seatInfo.add(new SeatInfo(tmpSeatBlock.getRow(), tmpSeatBlock.getCol()+i));
                    k++;
                }
            }
            //if we could not find requested number of seats, release them to availability pool
            if(k != numSeats) {
                for(SeatBlock seatBlockInternal : seatBlocks) {
                    availMap.putIfAbsent(seatBlockInternal.getLength(), new CopyOnWriteArrayList<>());
                    availMap.get(seatBlockInternal.getLength()).add(new SeatBlock(seatBlockInternal.getRow(), seatBlockInternal.getCol()));
                }
            } else {
                //create seatBlock hold with list of seatBlocks
                Instant expiryTime = Instant.now().plusMillis(holdPeriod);
                int holdId = holdIdGenerator.nextId();
                SeatBlockHold seatBlockHold = new SeatBlockHold(holdId,
                        seatBlocks,
                        expiryTime);
                holdQueue.offer(seatBlockHold);
                hold = new SeatHold(holdId,
                        customerEmail,
                        numSeats,
                        seatInfo,
                        expiryTime);
                freeSeats.updateAndGet((x) -> x - numSeats);
            }
        }
        return hold != null ?
                hold :
                new SeatHold(
                customerEmail,
                numSeats,
                new ErrorInfo("NOT_AVAILABLE", "The requested seats could not be allocated"));
    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        String resId = null;
        SeatBlockHold hold = new SeatBlockHold(seatHoldId);
        if(holdQueue.contains(hold)) {
            holdQueue.remove(hold);
            resId = resIdGenerator.nextId();
            reservedSeats.put(resId, hold);
        }
        return resId;
    }
}
