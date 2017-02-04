package npetzall.queue.api;

@FunctionalInterface
public interface Decoder<E> {
    E decode(byte[] data);
}
