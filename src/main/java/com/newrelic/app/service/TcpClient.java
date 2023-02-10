package com.newrelic.app.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This is the tcp client class that connects to the server, write a command and closes the connection
 */
public class TcpClient {
    private Socket socket;
    DataOutputStream writer;
    /**
     * Constructor
     * @param hostName - server name
     * @param port - port number where the server is listening
     */
    public TcpClient(String hostName, int port) {
        try {
            socket = new Socket(hostName, port);
            OutputStream out = socket.getOutputStream();
            writer = new DataOutputStream(out);
        } catch (IOException e) {
            System.out.printf("Client connect error %s\n", e.getMessage());
        }
    }

    /**
     * This function writes the command to the tcp server.
     * @param command - Command string to write which is either 9 digit string or "terminate" command
     */
    public void write(String command) {
        try {
            writer.write(String.format("%s\r\n", command).getBytes());
            writer.flush();
        } catch (IOException e) {
            System.out.printf("Client write error %s\n", e.getMessage());
        }
    }

    /**
     * Does the cleanup of the client by closing the socket and stream
     */
    public void shutdown() {
        try {
            writer.close();
            socket.close();
        } catch (IOException e) {
            System.out.printf("Client shutdown error %s\n", e.getMessage());
        }
    }
}
