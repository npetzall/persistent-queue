package npetzall.queue.api;

public interface Queue<E> {

    void enqueue(E element);
    E dequeue();

    E peek();
    void skip();

    Peeks<E> peek(int maxElements);
    void skip(Peeks<?> peeks);

    void clear();
}
