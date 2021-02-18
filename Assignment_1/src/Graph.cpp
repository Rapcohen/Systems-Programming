#include "Graph.h"
#include "Session.h"


// default constructor
Graph::Graph(std::vector <std::vector<int>> matrix) : edges(matrix), infected(matrix.size(), false) {}

// empty constructor
Graph::Graph() : edges(), infected() {}


void Graph::infectNode(int nodeInd) {
    infected[nodeInd] = true;
}


bool Graph::isInfected(int nodeInd) {
    return infected[nodeInd];
}

// Checks the termination condition of Session::simulate()
// Returns true *iff* each connected component of the Graph: (is fully infected) || (does not contain a virus)
// This function uses DFS logic - in an undirected Graph, DFS maps all of the connected components of the Graph
bool Graph::condition(const Session &session) {
    int size = this->getSize();
    bool bCondition = true;
    std::vector<char> color(size, 'w'); // [ 'w' == white , 'g' == gray , 'b' == black ]
    for (int nodeIdx = 0; nodeIdx < size && bCondition; ++nodeIdx) {
        if (color[nodeIdx] == 'w') {
            bool bSick = isInfected(nodeIdx);
            bCondition = dfsVisit(nodeIdx, bSick, color, session);
        }
    }
    return bCondition;
}

// DFS algorithm implementation
bool Graph::dfsVisit(int nodeIdx, bool bSick, std::vector<char> &color, const Session &session) {
    color[nodeIdx] = 'g';
    bool bContinueDfs;
    if (isInfected(nodeIdx) != bSick) {
        bContinueDfs = false;
    } else if (session.isCarrier(nodeIdx) && !isInfected(nodeIdx)) {
        bContinueDfs = false;
    } else {
        bContinueDfs = true;
        std::vector<int> neighbors(this->getNeighbors(nodeIdx));
        for (auto neighborIdx : neighbors) {
            if (color[neighborIdx] == 'w') {
                bContinueDfs = dfsVisit(neighborIdx, bSick, color, session);
                if (!bContinueDfs) break;
            }
        }
    }
    color[nodeIdx] = 'b';
    return bContinueDfs;
}


void Graph::removeEdge(int node1, int node2) {
    int matrixSize = getSize();
    if (node1 < matrixSize && node2 < matrixSize) {
        edges[node1][node2] = 0;
        edges[node2][node1] = 0;
    }
}


const std::vector <std::vector<int>> &Graph::getEdges() const {
    return edges;
}


// Returns a vector<int> containing the nodeLabels of all neighbors of the node
std::vector<int> Graph::getNeighbors(int nodeId) const {
    std::vector<int> neighbors{};
    for (size_t i = 0; i < edges[nodeId].size(); ++i) {
        if (edges[nodeId][i] == 1) {
            neighbors.push_back(i);
        }
    }
    return neighbors;
}


int Graph::getSize() const { return edges.size(); }
