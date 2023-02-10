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