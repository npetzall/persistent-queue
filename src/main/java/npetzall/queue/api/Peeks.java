package npetzall.queue.api;

import java.util.Iterator;

public interface Peeks<E> extends Iterable<E> {

    Iterator<Peek> peekIterator();

    @Override
    Iterator<E> iterator();

}

