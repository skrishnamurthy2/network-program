# TCP Server / TCP Client Java Program


## Install
* Requisites
  * Java 19
  * maven
* Installation and Running
  * mvn clean package

## Running as server  

mvn exec:java -Dexec.mainClass="com.newrelic.app.Application" -Dexec.args="-m server"
<br><br>Above command will start the server on port 4000, and will listen for client connection. Server will run infinitely until ^C or "terminate" message is received.
      
## Run as client

mvn exec:java -Dexec.mainClass="com.newrelic.app.Application" -Dexec.args="-m client -c 123456789"
<br><br>Above command will start the client connect to server on localhost in port 4000, and send 9 digit string "123456789" and then terminate.
<br><br>
mvn exec:java -Dexec.mainClass="com.newrelic.app.Application" -Dexec.args="-m client -c terminate"
<br><br>Above command is for sending termination signal to the server.
    
## Run unit test
mvn test - This command will run the unit test

## Command line arguments supported
-a,--address <arg>   Client mode only configuration - Server's ip address
or host name, default is 127.0.0.1
<br><br>
-c,--cmd <arg>       Client mode only configuration (required for client
mode) - command to write (either 9 digit number or
string 'terminate')
<br><br>
-m,--mode <arg>      Run in server/client mode
<br><br>
-n,--number <arg>    Server mode only configuration - Number of
concurrent clients, default is 5
<br><br>
-p,--port <arg>      Port number to listen on for server or for client to
connect to, default is 4000

## Assumptions
* Assumed there might be need to extend the number of concurrent clients allowed to be configurable so made it optional cmd line argument
* Thought it will be good to also have a client code to test the server logic
* Did not use any standard logger but created a simple version that logs in its own thread

## Test Cases Executed
* Server adds only unique 9 digit string
* Server does not add any digit string less than or greater than 9 digits
* Server logs the unique 9 digit string in number.log file
* Server reports every 10 seconds unique, duplicate and total unique 9 digits string received
* Server terminates when receiving "terminate" message