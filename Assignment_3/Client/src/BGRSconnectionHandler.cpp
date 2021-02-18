#include "BGRSconnectionHandler.h"

BGRSconnectionHandler::~BGRSconnectionHandler() {

}

BGRSconnectionHandler::BGRSconnectionHandler(std::string host, short port) : host_(host), port_(port), io_service_(),
                                                                             socket_(io_service_) {}

// Close down the connection properly.
void BGRSconnectionHandler::close() {
    try {
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

bool BGRSconnectionHandler::connect() {
    std::cout << "Starting connect to "
              << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception &e) {
        std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool BGRSconnectionHandler::getBytes(char *bytes, unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool BGRSconnectionHandler::sendBytes(const char *bytes, int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

bool BGRSconnectionHandler::sendMessage(std::string &msg) {
    std::vector <std::string> msgVector;
    boost::split(msgVector, msg, boost::is_any_of(" "));
    //Find and send message opcode
    short opcode = map[msgVector[0]];
    char opcodeBytes[2];
    shortToBytes(opcode, opcodeBytes);
    sendBytes(opcodeBytes, 2);
    //Send message attachments
    switch (opcode) {
        case 1:
        case 2:
        case 3: {
            sendBytes(msgVector[1].c_str(), msgVector[1].length() + 1);
            sendBytes(msgVector[2].c_str(), msgVector[2].length() + 1);
            break;
        }
        case 5:
        case 6:
        case 7:
        case 9:
        case 10: {
            short courseNum = boost::lexical_cast<short>(msgVector[1]);
            char courseNumBytes[2];
            shortToBytes(courseNum, courseNumBytes);
            sendBytes(courseNumBytes, 2);
            break;
        }
        case 8: {
            sendBytes(msgVector[1].c_str(), msgVector[1].length() + 1);
        }
    }
    return true;
}

void BGRSconnectionHandler::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

bool BGRSconnectionHandler::getMessage(std::string &part1, std::string &part2) {
    try {
        char opcodeBytes[2];
        if (getBytes(opcodeBytes, 2)) {
            char msgOpcodeBytes[2];
            if (getBytes(msgOpcodeBytes, 2)) {
                short opcode = bytesToShort(opcodeBytes);
                short msgOpcode = bytesToShort(msgOpcodeBytes);
                if (opcode == 12) { //ACK
                    part1.append("ACK " + std::to_string(msgOpcode));
                    char ch;
                    do {
                        if (!getBytes(&ch, 1)) return false;
                        if (ch != '\0') part2.append(1, ch);
                    } while (ch != '\0');
                    return true;
                } else { //ERR
                    part1.append("ERROR " + std::to_string(msgOpcode));
                    return true;
                }
            }
        }
    } catch (std::exception &e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
    }
    return false;
}

short BGRSconnectionHandler::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}