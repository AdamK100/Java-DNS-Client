# Java DNS Client

Simple command-line DNS client written from scratch in Java. Formats requests sent to a DNS server, and parses the response.

### Compiling the DNS Client:

To compile simply run `javac *.java` in the folder containing all the classes.

### Usage: 

The DnsClient takes into account the order of arguments. You need to write the arguments in the following order, otherwise the syntax will not be correct:

`java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name`

The standard port for DNS is port 53.

The program was built using java version 17.0.6.

