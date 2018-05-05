# Ticket Service

## Solution approach
The details to the ticket service implementation is described below.  

### Data structures

1. Ticket Hold area (Min heap) 
    - A hold will be added to the min heap on successful reservation hold. The min heap stores objects based on its 
    expiration timestamp. The expired entries cleaned up by scheduled timer on periodic intervals.
2. Available Seat pool (Map (block size -> seat blocks))
    - Stored as map of block size to available seatBlock blocks. For example a venue layout of 2 X 5, will store the available 
    blocks as 5 : (Row 1, starting col 1), (Row 2, starting col 1). 
3. Available Seat counter (AtomicInteger) 
    - gets updated on ticket hold and hold expiry.

### Hold/expiry:-
 The ticket holds are queued up in min heap using the hold's expiry timestamp. The service implementation holds a timer
  that runs in predefined interval (default: 1 second) to clean up the expired holds. The expired entries, if any, would
   be at the top of the min heap and could be easily found and released them to the availability pool. Th scheduled timer
    is started at service instantiation time and on each run, the heap's entries is examined and removed if expired. 

### Seat allocation:-
 The seatBlock allocation algorithm picks the first available block of seatBlocks that are adjacent to each other. If it couldn't
  find a contiguous block, it tries to split into multiple blocks starting the next lowest number than requested. If
   required seatBlocks could not be allocated, they would be released. The best seatBlock allocation is NP-hard and an alternate
    implementation (strategy pattern) could be used to allocate seatBlocks.
 
### Seat reservation using hold:-
 The seatBlock reservation operation uses the hold id and removes the hold from the hold queue. If the hold is valid/active,
  a reservation id is returned. If the hold couldn't be found, null is returned. 
 
    
## Error Handling

If the service receives inputs that could not be handled such as invalid venue layout, it returns IllegalArgumentException,
 a unchecked exception which doesn't violate the service contract. If a hold id could not be found for reservation 
 or sufficient seatBlocks could not be allocated, null would be returned.
    
## Assumptions

- The venue layout is expected to be two dimensional (rows/columns) where the seatBlocks in column are contiguous. 
- If the number of seatBlocks could not be allocated, then a null is returned. 
- The hold period (unit: milliseconds) is configurable at instantiation time. 

## Test cases
The service implementation is accompanied by a Junit test to test the hold/reservation behavior. It includes functional
 test cases, negative test cases and concurrent hold/reservation with random number of seatBlocks with sleep in between the requests.

## Sample input/output 
```
[REPL]Listening for commands. Valid commands include. 
Venue: RowCount ColumnCount HoldPeriod.
Hold: NumSeats Emailid.
Reserve: HoldId Emailid.
Example:
Venue: 2 5 30000
Hold: 2 todd@email.com
Reserve: 1 todd@email.com
Hold: 10 ryan@email.com
Venue: 2 5 30000
Available seats: 10
Hold: 2 todd@email.com
Hold status:SeatHold{holdId=1, numSeats=2, seats=[SeatInfo{row=1, col=1}, SeatInfo{row=1, col=2}], customerEmail='todd@email.com', expirationTime=2018-05-05T03:23:55.589Z, errorInfo=null}
Available seats: 8
Reserve: 1 todd@email.com
Reservation status:2283c58b-f222-430e-92cd-e1a04bc84e5f
Available seats: 8
Hold: 12 todd@email.com
Hold status:SeatHold{holdId=0, numSeats=0, seats=null, customerEmail='todd@email.com', expirationTime=null, errorInfo=ErrorInfo{errorCode='NOT_AVAILABLE', errorMessage='The requested seats could not be allocated'}}
Available seats: 8
bye

```

## Build/deployment steps

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)

```shell
mvn clean install
```

## Running the application locally

Option 1:

```shell
mvn exec:java
```

Option 2:
Kindly execute the `main` method in the `org.tics.ticket.solutions.Solution` class from your IDE.

