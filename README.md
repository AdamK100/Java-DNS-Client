# Java DNS Client

Simple command-line DNS client written from scratch in Java. Formats requests sent to a DNS server, and parses the response.

### Compiling the DNS Client:

To compile simply run `javac *.java` in the folder containing all the classes.

### Usage: 

The DnsClient takes into account the order of arguments. You need to write the arguments in the following order, otherwise the syntax will not be correct:

`java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name`

The standard port for DNS is port 53.

#### Examples:

`java DnsClient -t 5 -r 3 -p 53 -ns @8.8.8.8 google.com`

`java DnsClient -t 5 -r 3 -p 53 -mx @208.67.222.222 google.com`

The program was built using java version 17.0.6.

