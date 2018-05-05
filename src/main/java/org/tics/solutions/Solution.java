package org.tics.solutions;

import org.tics.ticket.TicketService;
import org.tics.ticket.TicketServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Solution {

    public static void main(String[] args) {

       /* int N = 2, M = 5;
        TicketServiceImpl impl = new TicketServiceImpl(N, M);
        assert(N*M == impl.numSeatsAvailable());
        for(int i=0;i<N*2;i++) {
            SeatHold hold = impl.findAndHoldSeats(2, "a@a.com");
            assert(N * M - (i+1)*2 == impl.numSeatsAvailable());
            assert(null != impl.reserveSeats(hold.getHoldId(), "a@a.com"));
//        System.out.println(impl.reserveSeats(hold.holdId, "a@a.com"));
            assert(N * M - (i+1)*2 == impl.numSeatsAvailable());
        }
        SeatHold hold = impl.findAndHoldSeats(2, "a@a.com");
        assert(null != hold);
        assert(0 == impl.numSeatsAvailable());
        hold = impl.findAndHoldSeats(1, "a@a.com");
        assert(null != hold);
        assert(0 == impl.numSeatsAvailable());
        impl.turnOffSchedule();*/
        Solution solution = new Solution();
        solution.printUsage();
        solution.doFromCommandLine();
    }

    public void doFromCommandLine() {
//        TicketServiceFactory factory = TicketServiceFactory.getDefault();
        Scanner scanner = new Scanner(System.in);
        String[] exitKeywords = new String[] {"quit", "exit", "bye"};
        List<String> exitKeywordsList = Arrays.asList(exitKeywords);
        TicketService service = null;
        while (scanner.hasNextLine()) {
            try {
                String inputStr = scanner.nextLine();
                if(exitKeywordsList.contains(inputStr.toLowerCase()))
                    break;
                String tkns[] = inputStr.split(" ");
                if(tkns.length < 3) {
                    printUsage();
                    continue;
                }
                switch(tkns[0].toLowerCase()) {
                    case "venue:":
                        int N = Integer.parseInt(tkns[1]);
                        int M = Integer.parseInt(tkns[2]);
                        int hold = Integer.parseInt(tkns[3]);
                        service = new TicketServiceImpl(N, M, hold);
                        System.out.println("Available seats: "+ service.numSeatsAvailable());
                        break;
                    case "hold:":
                        System.out.println(service.findAndHoldSeats(Integer.parseInt(tkns[1]), tkns[2]));
                        System.out.println("Available seats: "+ service.numSeatsAvailable());
                        break;
                    case "reserve:":
                        System.out.println(service.reserveSeats(Integer.parseInt(tkns[1]), tkns[2]));
                        System.out.println("Available seats: "+ service.numSeatsAvailable());
                        break;
                }
            } catch (Exception e) {
                System.out.println("Exception occurred while processing command. Please try again.");
            }
        }
    }

    void printUsage() {
        System.out.println("[REPL]Listening for commands. Valid commands include. \n" +
                "Venue: RowCount ColumnCount HoldPeriod.\n" +
                "Hold: NumSeats Emailid.\n" +
                "Reserve: HoldId Emailid.\n" +
                "Example:\n"+
                "Venue: 2 5 30000\n" +
                "Hold: 2 todd@email.com\n" +
                "Reserve: 1 todd@email.com\n" +
                "Hold: 10 ryan@email.com");
    }



}