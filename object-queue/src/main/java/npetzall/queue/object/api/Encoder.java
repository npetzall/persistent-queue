package npetzall.queue.object.api;

@FunctionalInterface
public interface Encoder<E> {
    byte[] encode(E element);
}
