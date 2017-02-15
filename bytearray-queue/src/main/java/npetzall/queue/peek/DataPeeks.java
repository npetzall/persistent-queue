package npetzall.queue.peek;

import npetzall.queue.api.Peek;
import npetzall.queue.api.Peeks;

import java.util.Iterator;
import java.util.List;

public class DataPeeks implements Peeks<byte[]> {

    private final List<Peek> peeks;

    public DataPeeks(List<Peek> peeks) {
        this.peeks = peeks;
    }

    @Override
    public Iterator<Peek> peekIterator() {
        return peeks.iterator();
    }

    @Override
    public Iterator<byte[]> iterator() {
        return new PeekDataIterator(peekIterator());
    }
}
