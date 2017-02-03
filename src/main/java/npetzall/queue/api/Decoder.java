package npetzall.queue.api;

public interface Decoder<E> {
    E decode(byte[] data);
}
