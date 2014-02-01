CC=g++
CFLAGS+=`pkg-config --cflags opencv`
CFLAGS+=-g
LDFLAGS+=`pkg-config --libs opencv`

PROG=YUVSample
OBJS=$(PROG).o

.PHONY: all clean
$(PROG): $(OBJS)
	$(CC) -o $(PROG) $(OBJS) $(LDFLAGS)

%.o: %.cpp
	$(CC) -c $(CFLAGS) $<

all: $(PROG)

clean:
	rm -f $(OBJS) $(PROG)

