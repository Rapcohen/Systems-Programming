#include "Agent.h"
#include "Tree.h"
#include "Session.h"


//-------------------------------
//============Agent==============
//-------------------------------

// default constructor
Agent::Agent() {}

// destructor
Agent::~Agent() {}


//-------------------------------
//=========ContactTracer=========
//-------------------------------

// default constructor
ContactTracer::ContactTracer() : Agent() {}

// destructor
ContactTracer::~ContactTracer() {}


void ContactTracer::act(Session &session) {
    if (!session.getInfectedQueue().empty()) {
        int infectedNode = session.dequeueInfected();
        Tree *pTreeRoot = Tree::createTree(session, infectedNode);
        pTreeRoot->bfs(session);
        int isolateNode = pTreeRoot->traceTree();
        delete pTreeRoot;
        Graph graph(session.getG());
        std::vector<int> neighbors = graph.getNeighbors(isolateNode);
        for (auto neighbor : neighbors) {
            graph.removeEdge(isolateNode, neighbor);
        }
        session.setGraph(graph);
    }
}


Agent *ContactTracer::clone() const {
    Agent *pClone = new ContactTracer(*this);
    return pClone;
}


//-------------------------------
//============Virus==============
//-------------------------------

// default constructor
Virus::Virus(int nodeInd) : Agent(), nodeInd(nodeInd) {}

// copy constructor
Virus::Virus(const Virus &other) : Agent(other), nodeInd(other.nodeInd) {}

// destructor
Virus::~Virus() {}


void Virus::act(Session &session) {
    Graph graph(session.getG());
    if (!graph.isInfected(nodeInd)) {
        graph.infectNode(nodeInd);
        session.enqueueInfected(nodeInd);
    }
    std::vector<int> neighbors = graph.getNeighbors(nodeInd);
    for (auto neighbor : neighbors) {
        if (!graph.isInfected(neighbor) && !session.isCarrier(neighbor)) {
            Virus copyVirus(neighbor);
            session.addAgent(copyVirus);
            session.makeCarrier(neighbor);
            break;
        }
    }
    session.setGraph(graph);
}


Agent *Virus::clone() const {
    Agent *pClone = new Virus(*this);
    return pClone;
}