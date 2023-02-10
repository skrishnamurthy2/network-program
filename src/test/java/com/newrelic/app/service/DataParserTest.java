package com.newrelic.app.service;

import com.newrelic.app.Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.net.Socket;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class DataParserTest {
    @Mock
    private Socket socket;
    @Mock
    private Application application;
    @Mock
    private DataCollector collector;

    @Test
    public void should_add_number_collector() throws Exception {
        DataParser parser = new DataParser(application, socket, collector);
        byte [] b = "123456789\r\n".getBytes();
        Mockito.when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(b));
        parser.run();
        Mockito.verify(collector, Mockito.times(1)).collect(anyString());
    }

    @Test
    public void should_issue_shutdown_when_terminate_received() throws Exception {
        DataParser parser = new DataParser(application, socket, collector);
        byte [] b = "terminate\r\n".getBytes();
        Mockito.when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(b));
        parser.run();
        Mockito.verify(application, Mockito.times(1)).shutdown();
        Mockito.verify(collector, Mockito.never()).collect(anyString());
    }

    @Test
    public void should_fail_when_input_is_not_numbers() throws Exception {
        DataParser parser = new DataParser(application, socket, collector);
        byte [] b = "invalid\r\n".getBytes();
        Mockito.when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(b));
        parser.run();
        Mockito.verify(socket, Mockito.times(1)).close();
        Mockito.verify(collector, Mockito.never()).collect(anyString());
    }

    @Test
    public void should_fail_when_input_is_less_than_9_digits() throws Exception {
        DataParser parser = new DataParser(application, socket, collector);
        byte [] b = "123\r\n".getBytes();
        Mockito.when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(b));
        parser.run();
        Mockito.verify(socket, Mockito.times(1)).close();
        Mockito.verify(collector, Mockito.never()).collect(anyString());
    }
}
