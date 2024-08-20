# Java DNS Client

Simple command-line DNS client written from scratch in Java. Formats requests sent to a DNS server, and parses the response.


### Usage: 

The DnsClient takes into account the order of arguments. You need to write the arguments in the following order, otherwise the syntax will not be correct:

`java DnsClient [-t timeout] [-r max-retries] [-p port] [-mx|-ns] @server name`

The program was built using java version 17.0.6.

