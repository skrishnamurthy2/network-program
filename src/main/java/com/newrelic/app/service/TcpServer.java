package com.newrelic.app.service;

import com.newrelic.app.Application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * This is the TcpServer that listens on a port for client connection, once a client is connected
 * it offloads the parsing the collecting logic by executing the DataParser run function through Runnable implementation.
 */
public class TcpServer {
    private int port;
    private ServerSocket socket;
    private boolean terminateReceived;
    private Application application;

    public TcpServer(Application application, int port) {
        this.port = port;
        terminateReceived = false;
        this.application = application;
    }

    /**
     * This is the main method that listens on the port and for each new connection delegates the parsing through the Executor.
     * @param executor - Executor service for executing the data receiving logic in a separate thread
     * @param dataCollector - DataCollector that does the collection of the numbers
     */
    public void listen(ExecutorService executor, DataCollector dataCollector) {
        try {
            socket = new ServerSocket(port);

            while (true) {
                Socket client = socket.accept();
                DataParser handler = new DataParser(application, client, dataCollector);
                executor.submit(handler);
            }
        } catch (IOException e) {
            if (!terminateReceived) {
                System.out.printf("server new connection error %s\n", e.getMessage());
            }
        } catch (Exception e) {
            System.out.printf("server new connection(unexpected) error %s\n", e.getMessage());
        }
    }

    /**
     * Triggers shutdown by closing the accept socket
     */
    public void shutdown() {
        terminateReceived = true;
        try {
            socket.close();
        } catch (IOException e) {
            System.out.printf("server shutdown error %s\n", e.getMessage());
        }
    }
}
