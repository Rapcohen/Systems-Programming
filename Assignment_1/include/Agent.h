#ifndef AGENT_H_
#define AGENT_H_

#include <vector>

class Session;

//-------------------------------
//============Agent==============
//-------------------------------
class Agent {
public:
    Agent();          // default constructor
    virtual ~Agent(); // destructor

    virtual void act(Session &session) = 0;

    virtual Agent *clone() const = 0;
};


//-------------------------------
//=========ContactTracer=========
//-------------------------------
class ContactTracer : public Agent {
public:
    ContactTracer();          // default constructor
    virtual ~ContactTracer(); // destructor

    virtual void act(Session &session);

    virtual Agent *clone() const;
};


//-------------------------------
//============Virus==============
//-------------------------------
class Virus : public Agent {
public:
    Virus(int nodeInd);        // default constructor
    Virus(const Virus &other); // copy constructor
    virtual ~Virus();          // destructor

    virtual void act(Session &session);

    virtual Agent *clone() const;

private:
    const int nodeInd;
};

#endif