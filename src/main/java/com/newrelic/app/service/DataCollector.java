package com.newrelic.app.service;

import com.newrelic.app.model.Constants;
import com.newrelic.app.model.DataCollected;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class collects the numbers received in the TCP network. The collected unique 9 digit numbers are kept
 * in a hash map. It also logs unique number string received using the Logger.
 */
public class DataCollector {
    private Map<String, Void> numbers = new HashMap<>();
    private Lock lock = new ReentrantLock();
    private Logger logger;
    private long duplicates;
    private long unique;
    private long totalUnique;

    public DataCollector(Logger logger) {
        this.logger = logger;
    }

    /**
     * This method collects the number string received in the TCP network. If it is not a valid
     * 9 digit number, it throws IllegalArgumentException.
     * @param number - 9 digit number string
     */
    public void collect(String number) throws IllegalArgumentException {
        boolean added = false;

        validate(number);

        lock.lock();
        try {
            if (!numbers.containsKey(number)) {
                numbers.put(number, null);
                unique++;
                totalUnique++;
                added = true;
            } else {
                duplicates++;
            }
        } finally {
            lock.unlock();
        }

        if (added) {
            logger.log(number);
        }
    }

    /**
     * This method returns the statistics of the collected number string details including
     * unique - unique number received since last report
     * duplicate - duplicate number received since the last report
     * totalUnique - total unique number received since the server has been running
     * @return The above stats are returned in DataCollected class
     */
    public DataCollected report() {
        lock.lock();
        try {
            DataCollected data = DataCollected.builder()
                    .unique(unique)
                    .duplicate(duplicates)
                    .totalUnique(totalUnique)
                    .build();

            unique = 0;
            duplicates = 0;
            return data;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Validates that string passed is a 9 digit number
     * @param number - 9 digit number String
     */
    private void validate(String number) {
        if (number == null || number.length() == 0 || number.length() > Constants.MAX_INPUT_LENGTH) {
            throw new IllegalArgumentException("String input size is invalid");
        }
        boolean invalid = number.chars().anyMatch(s -> s < '0' || s > '9');
        if (invalid) {
            throw new IllegalArgumentException("String input does not have just numbers");
        }
    }
}
