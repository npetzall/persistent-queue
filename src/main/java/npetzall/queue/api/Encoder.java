package npetzall.queue.api;

public interface Encoder<E> {
    byte[] encode(E element);
}
