# Solution for Ticket Service

# Requirements
The ticket service implementation should be written in Java
The solution and tests should build and execute entirely via the command line using either Maven or Gradle as the build tool
A README file should be included in your submission that documents your assumptions and includes instructions for building the
solution and executing the tests
Implementation mechanisms such as disk-based storage, a REST API, and a front-end GUI are not required


## Solution approach
The solution to this problem splits the concerns into separate package/classes. Each class is built to focus on one 
concern and is open for extension. 

The service implementation 

### Data structures

Available Seat counter - (AtomicInteger) - gets updated on ticket hold and hold expiry.
Ticket Hold - 			 (Min heap - expiry timestamp) - added on hold; expired entries cleaned up by scheduled timer 
Available Seat pool - (Map of block size to available seatBlock blocks) - 

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
  a reservation id is returned. If it couldn't be found, null is returned. 
 
    
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
Test input:
Venue: 2 5
Hold 2
Reserve 2
Hold 10
 
Test Output:
Hold id : 1
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

