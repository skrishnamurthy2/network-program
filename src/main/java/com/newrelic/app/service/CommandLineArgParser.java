package com.newrelic.app.service;

import com.newrelic.app.model.Arguments;
import com.newrelic.app.model.ServiceMode;
import com.newrelic.app.model.Constants;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Optional;

/**
 * This class uses the Apache commons-cli module to accept command line arguments.
 */
public class CommandLineArgParser {
    private Options commandLineOptions;

    /**
     * This is the main method of this class that parses the command line arguments and from that if successful
     * returns the value in Arguments class. If there is a failure it returns Optional.empty().
     * If no arguments is passed or if there is a parse failure, it prints help.
     * @param args - Command line arguments
     * @return
     */
    public Optional<Arguments> parse(String[] args) {

        setupCommandLineParser();

        Optional<Arguments> arg = parseCommandArguments(args);

        if (arg.isEmpty()) {
            System.out.println("Command line arguments not valid");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("newrelic-interview-app", commandLineOptions);
        }

        return arg;
    }

    /**
     * This method sets up the commons-cli Options for the parameters expected for this program to run
     */
    private void setupCommandLineParser() {
        commandLineOptions = new Options();

        Option mode = Option.builder()
                .option("m")
                .longOpt("mode")
                .hasArg(true)
                .required()
                .desc("Run in server/client mode").build();

        Option port = Option.builder()
                .option("p")
                .longOpt("port")
                .hasArg(true)
                .optionalArg(true)
                .desc("Port number to listen on for server or for client to connect to, default is " + Constants.DEFAULT_PORT).build();

        Option maxClient = Option.builder()
                .option("n")
                .longOpt("number")
                .hasArg(true)
                .optionalArg(true)
                .desc("Server mode only configuration - Number of concurrent clients, default is " + Constants.CONCURRENT_CLIENTS).build();

        Option serverAddress = Option.builder()
                .option("a")
                .longOpt("address")
                .hasArg(true)
                .optionalArg(true)
                .desc("Client mode only configuration - Server's ip address or host name, default is " + Constants.DEFAULT_SERVER_ADDRESS).build();

        Option command = Option.builder()
                .option("c")
                .longOpt("cmd")
                .hasArg(true)
                .optionalArg(true)
                .desc("Client mode only configuration (required for client mode) - command to write (either 9 digit number or string 'terminate') ").build();

        commandLineOptions.addOption(mode);
        commandLineOptions.addOption(port);
        commandLineOptions.addOption(maxClient);
        commandLineOptions.addOption(serverAddress);
        commandLineOptions.addOption(command);
    }

    /**
     * Once Option is setup in above method, this method does the actual parsing. If successful it returns non-empty Optional
     * otherwise returns empty Optional.
     * @param args - Command line arguments
     * @return - Arguments representing the fields needed for the application to run
     */
    private Optional<Arguments> parseCommandArguments(String[] args) {
        Arguments arg = Arguments.builder().build();

        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(commandLineOptions, args);

            String val = line.getOptionValue("m");
            ServiceMode mode = ServiceMode.getByMode(val);
            if (mode == null) {
                throw new ParseException("Not a valid application mode param 'm'");
            }
            arg.setServiceMode(mode);

            int port = Constants.DEFAULT_PORT;
            if (line.hasOption("p")) {
                val = line.getOptionValue("p");
                try {
                    port = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    throw new ParseException("Not a valid port number param 'p'");
                }
            }
            arg.setPortNumber(port);

            if (mode == ServiceMode.SERVER) {
                int max = Constants.CONCURRENT_CLIENTS;
                if (line.hasOption("n")) {
                    val = line.getOptionValue("n");
                    try {
                        max = Integer.parseInt(val);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Not a valid max concurrent clients param 'n'");
                    }
                }
                arg.setMaxConcurrentClients(max);
            } else {
                String defaultServer = Constants.DEFAULT_SERVER_ADDRESS;
                if (line.hasOption("a")) {
                    defaultServer = line.getOptionValue("a");
                }
                arg.setServerAddress(defaultServer);

                if (!line.hasOption("c")) {
                    throw new ParseException("Client command param 'c' is not passed");
                }
                arg.setClientCommand(line.getOptionValue("c"));
            }
        }
        catch (ParseException e) {
            System.out.println("Parsing failed terminating " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.out.println("Parsing failed(unexpected) terminating " + e.getMessage());
            return Optional.empty();
        }

        return Optional.of(arg);
    }
}
