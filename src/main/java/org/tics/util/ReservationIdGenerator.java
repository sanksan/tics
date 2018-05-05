package org.tics.util;

import java.util.UUID;

public class ReservationIdGenerator {


    public String nextId() {
        return UUID.randomUUID().toString();
    }

}