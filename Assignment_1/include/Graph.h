#ifndef GRAPH_H_
#define GRAPH_H_

#include <vector>

class Session;

class Graph {
public:
    Graph(std::vector <std::vector<int>> matrix); // default constructor
    Graph();                                      // empty constructor

    void infectNode(int nodeInd);

    bool isInfected(int nodeInd);

    bool condition(const Session &session);

    void removeEdge(int node1, int node2);

    // Getters
    const std::vector <std::vector<int>> &getEdges() const;

    std::vector<int> getNeighbors(int nodeId) const;

    int getSize() const;

private:
    std::vector <std::vector<int>> edges;
    std::vector<bool> infected; // Used to track which nodes are infected
    bool dfsVisit(int nodeIdx, bool bSick, std::vector<char> &vector, const Session &session);
};

#endif