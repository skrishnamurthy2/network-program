package com.newrelic.app.service;

import com.newrelic.app.model.Constants;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * This is the logger class that uses ExecutorService to actually do the writing to the file so that the
 * caller is not blocked
 */
public class Logger {
    private BufferedWriter writer;
    private ExecutorService executorService;

    /**
     * Constructor
     * @param service - Executor Service to use for writing the log in a separate thread
     */
    public Logger(ExecutorService service) {
        this.executorService = service;
        try {
            FileWriter fw = new FileWriter(Constants.LOG_FILE, false);
            writer = new BufferedWriter(fw);
        } catch (IOException e) {
            System.out.printf("log file open error %s\n", e.getMessage());
        }
    }

    /**
     * This is the main method that does the writing of the numbers received to number.log file
     * @param number - Number string received
     */
    public void log(String number) {
        executorService.submit(() -> {
            try {
                writer.write(number);
                writer.newLine();
            } catch (IOException e) {
                System.out.printf("log error %s\n", e.getMessage());
            }
        });
    }

    /**
     * Called to close the log file
     */
    public void shutdown() {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.printf("log file shutdown error %s\n", e.getMessage());
        }
    }
}
