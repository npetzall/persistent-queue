package npetzall.queue.object.api;

@FunctionalInterface
public interface Decoder<E> {
    E decode(byte[] data);
}
