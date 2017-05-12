JFLAGS = -g
BIN = bin/

SRCES = $(shell find . -type f -name '*.java')

all: compile

compile: 
	javac -d $(BIN) $(SRCES) 

runbasic: 
	java -cp $(BIN) algorithm.Main

run:
	java -cp $(BIN) algorithm.Main ./data/sprint01.xml

exp: 
	bash exp.sh

clean:
	rm -rf bin/*