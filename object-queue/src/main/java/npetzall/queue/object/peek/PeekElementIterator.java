package npetzall.queue.object.peek;

import npetzall.queue.object.api.Decoder;

import java.util.Iterator;

public class PeekElementIterator<T> implements Iterator<T> {

    private final Decoder<T> decoder;
    private final Iterator<byte[]> iterator;

    public PeekElementIterator(Decoder<T> decoder, Iterator<byte[]> iterator) {
        this.decoder = decoder;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return decoder.decode(iterator.next());
    }

    @Override
    public void remove() {
        iterator.remove();
    }
}
