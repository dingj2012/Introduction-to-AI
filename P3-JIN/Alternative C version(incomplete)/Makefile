PROGRAMS = recognize

.PHONY: all clean

all: $(PROGRAMS)

%: %.c
	gcc -Wall $^ -o $@

clean:
	rm -rf *~ $(PROGRAMS) sentence-optimized.hmm

test:
	./recognize sentence.hmm example1.obs
 
