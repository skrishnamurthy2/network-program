package com.newrelic.app;

import com.newrelic.app.model.Arguments;
import com.newrelic.app.model.ServiceMode;
import com.newrelic.app.service.CommandLineArgParser;
import com.newrelic.app.service.DataCollector;
import com.newrelic.app.service.Logger;
import com.newrelic.app.service.Reporter;
import com.newrelic.app.service.TcpClient;
import com.newrelic.app.service.TcpServer;

import java.util.Optional;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is the main class of the project that after accepting command line arguments, either runs as server or client.
 * Launch the application to know all the arguments needed, server mode and client mode requires different set of arguments
 */
public class Application
{
    private TcpServer server;
    private TcpClient client;
    private DataCollector dataCollector;
    private Logger logger;
    private Reporter reporter;
    private Timer timer;
    private ExecutorService executor;
    private ExecutorService logExecutor;

    /**
     * This is the main method of the class which after parsing the arguments either runs in server or client mode
     * @param args - Command lime arguments
     */
    public static void main(String[] args)
    {
        Application app = new Application();
        CommandLineArgParser parser = new CommandLineArgParser();
        Optional<Arguments> arg = parser.parse(args);
        if (arg.isEmpty()) {
            return;
        }
        if (arg.get().getServiceMode() == ServiceMode.SERVER) {
            app.runServer(arg.get());
        } else {
            app.runClient(arg.get());
        }
    }

    /**
     * This method launches the application in server mode. listen method does not return so either
     * client needs to send "terminate" command or kill the process by ^C.
     * @param arg - Parsed command line arguments needed for running in server mode.
     */
    private void runServer(Arguments arg) {
        try {
            initialize(arg);
            server.listen(executor, dataCollector);
            cleanup();
            dataCollector.report();
        } catch (Exception e) {
            System.out.println("Exception occurred during shutdown " + e.getMessage());
        }
        System.out.println("server listen terminated");
    }

    /**
     * This method launches the application in client mode. Application connects to TCP server
     * and write command passed in the argument which can be either 9 digits number of "terminate" string.
     * @param arg - Arguments needed to run in client mode
     */
    private void runClient(Arguments arg) {
        client = new TcpClient(arg.getServerAddress(), arg.getPortNumber());
        client.write(arg.getClientCommand());
        client.shutdown();
    }

    /**
     * This method is called by the TcpServer on receiving "terminate" command from the client.
     * It stops the periodical reporting timer and closes the listening socket to prevent new connections.
     * listen method in runServer will come out when this happens, and cleanup of rest of the data happens there.
     */
    public void shutdown() {
        try {
            timer.cancel();
            server.shutdown();
            System.out.println("shutdown complete");
        } catch (Exception e) {
            System.out.println("Exception occurred during termination " + e.getMessage());
        }
    }

    /**
     * This method sets up the needed services for use by the runServer.
     * @param arg - Parsed command line arguments
     */
    private void initialize(Arguments arg) {
        executor = Executors.newFixedThreadPool(arg.getMaxConcurrentClients());
        logExecutor = Executors.newFixedThreadPool(1);
        server = new TcpServer(this, arg.getPortNumber());
        logger = new Logger(logExecutor);
        dataCollector = new DataCollector(logger);
        reporter = new Reporter(dataCollector);
        timer = new Timer();
        timer.scheduleAtFixedRate(reporter, 10000, 10000);
    }

    /**
     * This method does mostly the threadpool cleanup when server shutdown is initiated.
     * @throws InterruptedException
     */
    private void cleanup() throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
            System.out.println("Threads did not terminate");
        }
        logExecutor.shutdown();
        if (!logExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
            System.out.println("Log Threads did not terminate");
        }
        logger.shutdown();
    }
}
