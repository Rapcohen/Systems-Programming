all: BGRSclient

BGRSclient: bin/BGRSclient.o bin/BGRSconnectionHandler.o 
	g++ -o bin/BGRSclient bin/BGRSclient.o bin/BGRSconnectionHandler.o -lboost_system -pthread

bin/BGRSclient.o: src/BGRSclient.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/BGRSclient.o src/BGRSclient.cpp

bin/BGRSconnectionHandler.o: src/BGRSconnectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/BGRSconnectionHandler.o src/BGRSconnectionHandler.cpp
	
clean: 
	rm -f bin/*