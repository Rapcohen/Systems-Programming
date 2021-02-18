#ifndef TREE_H_
#define TREE_H_

#include <vector>

class Session;

//------------------------------
//============Tree==============
//------------------------------
class Tree {
public:
    // Rule of 5
    Tree(int rootLabel);                        // default constructor
    Tree(const Tree &other);                    // copy constructor
    Tree(Tree &&other);                         // move constructor
    const Tree &operator=(const Tree &other);   // copy assignment
    Tree &operator=(Tree &&other);              // move assignment
    virtual ~Tree();                            // destructor

    void addChild(const Tree &child);

    void addChild(Tree *child);

    static Tree *createTree(const Session &session, int rootLabel);

    virtual int traceTree() = 0;

    void bfs(const Session &session);

    static Tree *compare(Tree *node1, Tree *node2);

    Tree *maxChild(int depth); // Recursive function used in traceTree
    virtual Tree *clone() const = 0;

    // Getters & Setters
    int getTime() const;
    int getDepth() const;
    int getNode() const;
    const std::vector<Tree *> &getChildren() const;
    void setDepth(int _depth);

protected:
    int time;
    int depth;

private:
    int node;
    std::vector<Tree *> children;
};

//----------------------------------
//============CycleTree=============
//----------------------------------
class CycleTree : public Tree {
public:
    CycleTree(int rootLabel, int currCycle); // default constructor

    virtual int traceTree();

    virtual Tree *clone() const;

private:
    int currCycle;
};

//-------------------------------
//============MaxRankTree========
//-------------------------------
class MaxRankTree : public Tree {
public:
    MaxRankTree(int rootLabel); // default constructor

    virtual int traceTree();

    virtual Tree *clone() const;

};

//----------------------------------
//============RootTree==============
//----------------------------------
class RootTree : public Tree {
public:
    RootTree(int rootLabel); // default constructor

    virtual int traceTree();

    virtual Tree *clone() const;
};

#endif
