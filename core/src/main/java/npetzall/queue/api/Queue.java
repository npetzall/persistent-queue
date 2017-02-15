package npetzall.queue.api;

public interface Queue<E> {

    boolean enqueue(E element);

    E deque();

    E peek();
    void skip();

    Peeks<E> peek(int maxElements);
    void skip(Peeks<?> peeks);

    void clear();

    void close();
}
