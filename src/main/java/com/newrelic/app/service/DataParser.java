package com.newrelic.app.service;

import com.newrelic.app.Application;
import com.newrelic.app.model.Constants;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class does the parsing and validation of the data received from network. It ensures
 * that the data received is either 9 digit string or "terminate" command. It implements
 * Runnable so that the caller can execute this using different thread.
 */
public class DataParser implements Runnable {
    private Socket clientSocket;
    private Application application;
    private DataCollector dataCollector;

    /**
     * Constructor
     * @param application - Application object to initiating shutdown when "terminate" is received
     * @param clientSocket - socket of the new client connected
     * @param dataCollector - DataCollector to receive the 9 digit number to be sent by the client
     */
    public DataParser(Application application, Socket clientSocket, DataCollector dataCollector) {
        this.clientSocket = clientSocket;
        this.application = application;
        this.dataCollector = dataCollector;
    }

    /**
     * This method does the parsing of the received data. It validates the input is 9 digit string or terminate string.
     * if 9 digit string, it calls the data collector to store the string. if terminate, it informs the application object
     * to initiate shutdown of the server.
     */
    @Override
    public void run() {
        try {
            clientSocket.setSoTimeout(Constants.READ_TIMEOUT);
            DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

            StringBuilder number = new StringBuilder();
            for (int i = 0; i < Constants.MAX_INPUT_LENGTH; i++) {
                byte data = in.readByte();
                if (data >= '0' && data <= '9') {
                    number.append((char)data);
                } else {
                    boolean closeSocket = i != 0 || !checkShouldTerminate(data, in);
                    if (closeSocket) {
                        clientSocket.close();
                    }
                    return;
                }
            }
            byte data = in.readByte();
            if (data != '\r') {
                clientSocket.close();
                return;
            }
            data = in.readByte();
            if (data != '\n') {
                clientSocket.close();
                return;
            }

            dataCollector.collect(number.toString());
            clientSocket.close();
        } catch (IOException e) {
            System.out.printf("server data receive error %s\n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("server data receive error (unexpected) %s\n", e.getMessage());
        }
    }

    /**
     * Checks if the data is "terminate" command
     * @param data - The initial first character received in the network
     * @param in - Rest of the input stream of data
     * @return - returns true if "terminate" is received false otherwise
     */
    private boolean checkShouldTerminate(byte data, DataInputStream in) {
        int index = 0;
        if (Constants.TERMINATE_COMMAND.charAt(index) != data) {
            return false;
        }
        index++;
        try {
            for (int i = index; i < Constants.TERMINATE_COMMAND.length(); i++) {
                data = in.readByte();
                if (Constants.TERMINATE_COMMAND.charAt(i) != data) {
                    return false;
                }
            }
            data = in.readByte();
            if (data != '\r') {
                return false;
            }
            data = in.readByte();
            if (data != '\n') {
                return false;
            }
        } catch (IOException e) {
            System.out.printf("server data receive error while checking for termination %s\n", e.getMessage());
        } catch (Exception e) {
            System.out.printf("server data receive error(unexpected) while checking for termination %s\n", e.getMessage());
        }
        application.shutdown();
        return true;
    }
}
