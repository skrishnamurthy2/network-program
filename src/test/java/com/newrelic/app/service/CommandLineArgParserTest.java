package com.newrelic.app.service;

import com.newrelic.app.model.Arguments;
import com.newrelic.app.model.Constants;
import com.newrelic.app.model.ServiceMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class CommandLineArgParserTest {
    @Test
    public void should_accept_server_mode() {
        CommandLineArgParser parser = new CommandLineArgParser();
        Optional<Arguments> arguments = parser.parse("-m server".split(" "));
        Assertions.assertEquals(ServiceMode.SERVER, arguments.get().getServiceMode());
        Assertions.assertEquals(Constants.DEFAULT_PORT, arguments.get().getPortNumber());
        Assertions.assertEquals(Constants.CONCURRENT_CLIENTS, arguments.get().getMaxConcurrentClients());
    }

    @Test
    public void should_accept_server_mode_different_port() {
        CommandLineArgParser parser = new CommandLineArgParser();
        Optional<Arguments> arguments = parser.parse("-m server -p 2000".split(" "));
        Assertions.assertEquals(ServiceMode.SERVER, arguments.get().getServiceMode());
        Assertions.assertEquals(2000, arguments.get().getPortNumber());
    }

    @Test
    public void should_accept_server_mode_concurrent_client() {
        CommandLineArgParser parser = new CommandLineArgParser();
        Optional<Arguments> arguments = parser.parse("-m server -p 2000 -n 10".split(" "));
        Assertions.assertEquals(ServiceMode.SERVER, arguments.get().getServiceMode());
        Assertions.assertEquals(10, arguments.get().getMaxConcurrentClients());
    }

    @Test
    public void should_accept_client_mode() {
        CommandLineArgParser parser = new CommandLineArgParser();
        Optional<Arguments> arguments = parser.parse("-m client -c terminate".split(" "));
        Assertions.assertEquals(ServiceMode.CLIENT, arguments.get().getServiceMode());
        Assertions.assertEquals(Constants.DEFAULT_PORT, arguments.get().getPortNumber());
        Assertions.assertEquals(Constants.DEFAULT_SERVER_ADDRESS, arguments.get().getServerAddress());
        Assertions.assertEquals("terminate", arguments.get().getClientCommand());
    }
}
