#include "Tree.h"
#include "Session.h"
#include <queue>

//------------------------------
//============Tree==============
//------------------------------

// default constructor
Tree::Tree(int rootLabel) : time(-1), depth(0), node(rootLabel), children() {}

// copy constructor
Tree::Tree(const Tree &other) : time(other.time), depth(other.depth), node(other.node), children() {
    for (auto child : other.children) {
        addChild(*child);
    }
}

// move constructor
Tree::Tree(Tree &&other) : time(other.time), depth(other.depth), node(other.node), children() {
    for (auto child : other.children) {
        Tree * pChild = child;
        children.push_back(pChild);
        child = nullptr;
    }
    other.children.clear();
}

// copy assignment
const Tree &Tree::operator=(const Tree &other) {
    if (this != &other) {
        time = other.time;
        depth = other.depth;
        node = other.node;
        for (auto child : children) {
            if (child && child != &other) { // edge case: "other" is a refrence to one of "this" children.
                delete child;
            }
        }
        children.clear();
        for (auto otherChild : other.children) {
            addChild(*otherChild);
        }
    }
    return *this;
}

// move assignment
Tree &Tree::operator=(Tree &&other) {
    if (this != &other) {
        time = other.time;
        depth = other.depth;
        node = other.node;
        for (auto child : children) {
            if (child) {
                delete child;
            }
        }
        children.clear();
        for (auto otherChild : other.children) {
            Tree *pChild = otherChild;
            children.push_back(pChild);
            otherChild = nullptr;
        }
        other.children.clear();
    }
    return *this;
}

// destructor
Tree::~Tree() {
    for (auto subTree : children) {
        delete subTree;
    }
}


void Tree::addChild(const Tree &child) {
    // Dynamic memory allocation of Tree object happens in Tree::clone()
    Tree *pTree = child.clone();
    children.push_back(pTree);
}


void Tree::addChild(Tree *child) {
    children.push_back(child);
}


Tree *Tree::createTree(const Session &session, int rootLabel) {
    Tree *tree;
    switch (session.getTreeType()) {
        case Cycle:
            tree = new CycleTree(rootLabel, session.getCycle());
            break;
        case MaxRank:
            tree = new MaxRankTree(rootLabel);
            break;
        case Root:
            tree = new RootTree(rootLabel);
            break;
    }
    return tree;
}

// Creates a BFS tree (this == root)
void Tree::bfs(const Session &session) {
    const Graph &graph = session.getG();
    std::queue < Tree * > bfsQueue;
    bfsQueue.push(this);
    int timeDiscovered = 0;
    std::vector<bool> visited(graph.getSize(), false); // array that keeps track of visited nodes in the bfs algorithm
    visited[this->getNode()] = true;
    while (!(bfsQueue.empty())) {
        Tree *pTreeNode = bfsQueue.front();
        pTreeNode->time = timeDiscovered;
        bfsQueue.pop();
        std::vector<int> neighbors(graph.getNeighbors(pTreeNode->node));
        for (auto nodeIdx : neighbors) {
            if (!visited[nodeIdx]) {
                visited[nodeIdx] = true;
                Tree *pChild = createTree(session, nodeIdx);
                pTreeNode->addChild(pChild);
                bfsQueue.push(pChild);
            }
        }
        timeDiscovered++;
    }
}

// Given 2 Tree nodes - returns a pointer to one node according to the following logic:
// (1) most children | (2) smallest depth | (3) left-most node
Tree *Tree::compare(Tree *node1, Tree *node2) {
    if (node1 == node2) { return node1; }
    if (node1->getChildren().size() == node2->getChildren().size()) {
        if (node1->getDepth() == node2->getDepth()) {
            if (node1->getTime() < node2->getTime()) {
                return node1;
            } else {
                return node2;
            }
        } else { // node1->depth != node2->depth
            if (node1->getDepth() < node2->getDepth()) {
                return node1;
            } else {
                return node2;
            }
        }
    } else { // node1->children_size != node2->children_size
        if (node1->getChildren().size() > node2->getChildren().size()) {
            return node1;
        } else {
            return node2;
        }
    }
}

// Returns the maximum rank node in the node's subtree (recursive)
Tree *Tree::maxChild(int depth) {
    if (this->getChildren().empty()) {
        return this;
    } else {
        Tree *pMaxNode = this->getChildren()[0];
        for (auto child : this->getChildren()) {
            child->setDepth(depth);
            Tree *pMaxChild = child->maxChild(depth + 1);

            pMaxNode = compare(pMaxNode, pMaxChild);
        }
        return compare(this, pMaxNode);
    }
}


int Tree::getTime() const {
    return time;
}


int Tree::getDepth() const {
    return depth;
}


int Tree::getNode() const {
    return node;
}


const std::vector<Tree *> &Tree::getChildren() const {
    return children;
}


void Tree::setDepth(int _depth) {
    Tree::depth = _depth;
}


//----------------------------------
//============CycleTree=============
//----------------------------------

// default constructor
CycleTree::CycleTree(int rootLabel, int currCycle) : Tree(rootLabel), currCycle(currCycle) {}


int CycleTree::traceTree() {
    Tree *pCurrNode = this;
    bool bLastNode = false;
    for (int i = 0; i < currCycle && !bLastNode; ++i) {
        if (!pCurrNode->getChildren().empty()) {
            pCurrNode = pCurrNode->getChildren().front();
        } else {
            bLastNode = true;
        }
    }
    return pCurrNode->getNode();
}


Tree *CycleTree::clone() const {
    Tree *pClone = new CycleTree(*this);
    return pClone;
}


//-------------------------------
//============MaxRankTree========
//-------------------------------

// default constructor
MaxRankTree::MaxRankTree(int rootLabel) : Tree(rootLabel) {}


int MaxRankTree::traceTree() {
    Tree *maxNode = this->maxChild(1);
    return maxNode->getNode();
}


Tree *MaxRankTree::clone() const {
    Tree *pClone = new MaxRankTree(*this);
    return pClone;
}


//----------------------------------
//============RootTree==============
//----------------------------------

// default constructor
RootTree::RootTree(int rootLabel) : Tree(rootLabel) {}


int RootTree::traceTree() {
    return this->getNode();
}


Tree *RootTree::clone() const {
    Tree *pClone = new RootTree(*this);
    return pClone;
}
