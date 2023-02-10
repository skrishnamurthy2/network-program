package com.newrelic.app.service;

import com.newrelic.app.model.DataCollected;

import java.util.TimerTask;

/**
 * This is the reporting class that reports on the console statistics of the numbers received by getting the information
 * from the DataCollector class. run is called periodically using Timer.
 */
public class Reporter extends TimerTask {
    private DataCollector collector;
    public Reporter(DataCollector collector) {
        this.collector = collector;
    }

    /**
     * This function does the reporting of the stats on console by getting the info from DataCollector.
     */
    @Override
    public void run() {
        DataCollected data = collector.report();
        System.out.printf("Received %d unique numbers, %d duplicates. Unique total: %d\n",
                data.getUnique(), data.getDuplicate(), data.getTotalUnique());
    }
}
