#ifndef BOOST_CLIENT_BGRSCONNECTIONHANDLER_H
#define BOOST_CLIENT_BGRSCONNECTIONHANDLER_H

#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <boost/algorithm/string.hpp>
#include <unordered_map>
#include <boost/lexical_cast.hpp>


using boost::asio::ip::tcp;

class BGRSconnectionHandler {
private:
    const std::string host_;
    const short port_;
    boost::asio::io_service io_service_;   // Provides core I/O functionality
    tcp::socket socket_;
    std::unordered_map<std::string, short> map = {{"ADMINREG",     1},
                                                  {"STUDENTREG",   2},
                                                  {"LOGIN",        3},
                                                  {"LOGOUT",       4},
                                                  {"COURSEREG",    5},
                                                  {"KDAMCHECK",    6},
                                                  {"COURSESTAT",   7},
                                                  {"STUDENTSTAT",  8},
                                                  {"ISREGISTERED", 9},
                                                  {"UNREGISTER",   10},
                                                  {"MYCOURSES",    11}};


public:
    BGRSconnectionHandler(std::string host, short port);

    virtual ~BGRSconnectionHandler();

    // Connect to the remote machine
    bool connect();

    // Read a fixed number of bytes from the server - blocking.
    // Returns false in case the connection is closed before bytesToRead bytes can be read.
    bool getBytes(char bytes[], unsigned int bytesToRead);

    // Send a fixed number of bytes from the client - blocking.
    // Returns false in case the connection is closed before all the data is sent.
    bool sendBytes(const char bytes[], int bytesToWrite);

    bool sendMessage(std::string &msg);

    bool getMessage(std::string &part1, std::string &part2);


    // Close down the connection properly.
    void close();

    void shortToBytes(short num, char *bytesArr);

    short bytesToShort(char *bytesArr);
}; //class ConnectionHandler

#endif
