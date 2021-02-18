#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <string>
#include <queue>
#include "Graph.h"

class Agent;

enum TreeType {
    Cycle,
    MaxRank,
    Root
};

class Session {
public:
    // Rule of 5
    Session(const std::string &path);               // default constructor
    Session(const Session &other);                  // copy constructor
    Session(Session &&other);                       // move constructor
    const Session &operator=(const Session &other); // copy assignment
    Session &operator=(Session &&other);            // move assignment
    virtual ~Session();                             // destructor

    void simulate();

    void addAgent(const Agent &agent);

    void setGraph(const Graph &graph);

    void enqueueInfected(int);

    int dequeueInfected();

    // Getters
    const Graph &getG() const;
    TreeType getTreeType() const;
    const std::queue<int> &getInfectedQueue() const;
    int getCycle() const;

    void createOutputJson();

    bool isCarrier(int nodeIndex) const;

    void makeCarrier(int nodeIndex);

private:
    Graph g;
    TreeType treeType;
    std::vector<Agent *> agents;
    std::queue<int> infectedQueue;
    std::vector<bool> carriers; // Keeps track of the nodes which carry a virus (including nodes which are not infected)
    int cycle;
};

#endif