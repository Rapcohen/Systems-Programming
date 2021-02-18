#define BUFSIZE 1024

#include <stdlib.h>
#include <BGRSconnectionHandler.h>
#include <thread>
#include <queue>
#include <mutex>
#include <condition_variable>

template<typename T>
class LockingQueue {
public:
    LockingQueue() : queue(), guard(), signal() {}

    void push(T const &_data) {
        {
            std::lock_guard <std::mutex> lock(guard);
            queue.push(_data);
        }
        signal.notify_one();
    }

    void waitAndPop(T &_value) {
        std::unique_lock <std::mutex> lock(guard);
        while (queue.empty()) {
            signal.wait(lock);
        }
        _value = queue.front();
        queue.pop();
    }

private:
    std::queue <T> queue;
    mutable std::mutex guard;
    std::condition_variable signal;
};

class KeyboardListener {
private:
    bool &shouldTerminate;
    std::condition_variable &cv;
    std::mutex &logoutLock;
    char buf[BUFSIZE];
    LockingQueue<std::string> *msgQueue;
public:
    KeyboardListener(LockingQueue<std::string> *msgQueue, std::mutex &logoutLock, bool &term,
                     std::condition_variable &cv) : shouldTerminate(term),
                                                    cv(cv), logoutLock(logoutLock), buf(), msgQueue(msgQueue) {}

    void run() {
        while (!shouldTerminate) {
            std::cin.getline(buf, BUFSIZE);
            std::string msg(buf);
            msgQueue->push(msg);
            if (msg == "LOGOUT") {
                std::unique_lock <std::mutex> lock(logoutLock);
                cv.wait(lock);
            }
        }
    }
};

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    BGRSconnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    LockingQueue<std::string> *msgQueue = new LockingQueue<std::string>();
    bool term = false;
    std::mutex msgLock;
    std::condition_variable cv;
    KeyboardListener listener(msgQueue, msgLock, term, cv);
    //Start Keyboard listener thread
    std::thread listenerThread(&KeyboardListener::run, &listener);

    while (!term) {
        std::string line;
        msgQueue->waitAndPop(line);
        //Send message to server
        if (!connectionHandler.sendMessage(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        std::string part1;
        std::string part2;

        //Get answer form server
        if (!connectionHandler.getMessage(part1, part2)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        //Print answer to client console
        std::cout << part1 << std::endl;
        if (!part2.empty()) {
            std::cout << part2 << std::endl;
        }
        //Check for termination condition due to logout
        if (part1 == "ACK 4" || part1 == "ERROR 4") {
            {//Local scope for lock_guard
                std::lock_guard <std::mutex> lk(msgLock);
                term = (part1 == "ACK 4");
            }
            //Notify keyboard listener thread
            cv.notify_all();
        }
    }
    listenerThread.join();
    delete msgQueue;
    return 0;
}
