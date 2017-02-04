package npetzall.queue.api;

@FunctionalInterface
public interface Encoder<E> {
    byte[] encode(E element);
}
