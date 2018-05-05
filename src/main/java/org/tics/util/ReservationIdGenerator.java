package org.tics.util;

import java.util.UUID;

/**
 * Generator for reservation id.
 */
public class ReservationIdGenerator {


    public String nextId() {
        return UUID.randomUUID().toString();
    }

}