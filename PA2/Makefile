all: clean_out output clean

output: PA2.o 
	gcc PA2.o -o output

PA2.o: PA2.c
	gcc -c PA2.c

clean_out:
	rm -rf output
	rm -rf *.o

clean:
	rm -rf *.o
