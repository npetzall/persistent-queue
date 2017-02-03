package npetzall.queue.peek;

import npetzall.queue.api.Peek;

import java.util.Iterator;

public class PeekDataIterator implements Iterator<byte[]> {

    private final Iterator<Peek> peekIterator;

    public PeekDataIterator(Iterator<Peek> peekIterator) {
        this.peekIterator = peekIterator;
    }

    @Override
    public boolean hasNext() {
        return peekIterator.hasNext();
    }

    @Override
    public byte[] next() {
        return peekIterator.next().getData();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not allowed");
    }
}
