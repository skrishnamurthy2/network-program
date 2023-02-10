package com.newrelic.app.model;

import lombok.Builder;
import lombok.Data;

/**
 * This class represents the fields needed to run the application in server or client mode
 */
@Builder
@Data
public class Arguments {
    private ServiceMode serviceMode;
    private int portNumber;
    private String serverAddress;
    private int maxConcurrentClients;
    private String clientCommand;
}
