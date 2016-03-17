PROGRAMS = recognize statepath optimize

.PHONY: all clean

all: $(PROGRAMS)

%: %.java
	javac $^

clean:
	rm -rf recognize.class statepath.class optimize.class sentence-optimized.hmm

testR:
	java recognize sentence.hmm example1.obs

testS:
	java statepath sentence.hmm example1.obs

testO:
	java optimize sentence.hmm example2.obs sentence-optimized.hmm
