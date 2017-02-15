package npetzall.queue.object.peek;

import npetzall.queue.api.Peek;
import npetzall.queue.api.Peeks;
import npetzall.queue.object.api.Decoder;

import java.util.Iterator;

public class ElementPeeks<T> implements Peeks<T> {

    private final Decoder<T> decoder;
    private final Peeks<byte[]> byteArrayPeeks;

    public ElementPeeks(Decoder<T> decoder, Peeks<byte[]> byteArrayPeeks) {
        this.decoder = decoder;
        this.byteArrayPeeks = byteArrayPeeks;
    }

    @Override
    public Iterator<Peek> peekIterator() {
        return byteArrayPeeks.peekIterator();
    }

    @Override
    public Iterator<T> iterator() {
        return new PeekElementIterator<>(decoder, byteArrayPeeks.iterator());
    }
}
