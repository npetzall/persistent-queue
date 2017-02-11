package npetzall.queue;

import npetzall.queue.api.*;
import npetzall.queue.peek.ElementPeeks;

import java.io.IOException;

public class PersistentQueue<E> implements Queue<E> {

    private final Encoder<E> encoder;
    private final Decoder<E> decoder;
    private final Queue<byte[]> queue;

    public PersistentQueue(Queue<byte[]> queue, Encoder<E> encoder, Decoder<E> decoder) throws IOException {
        this.encoder = encoder;
        this.decoder = decoder;
        this.queue = queue;
    }

    @Override
    public boolean enqueue(E element) {
        byte[] elementBytes = encoder.encode(element);
        if (elementBytes.length > 0) {
            return queue.enqueue(elementBytes);
        }
        return false;
    }

    @Override
    public E dequeue() {
        byte[] element = queue.dequeue();
        if (element.length == 0) {
            return null;
        }
        return decoder.decode(element);
    }

    @Override
    public E peek() {
        byte[] element = queue.peek();
        if (element.length == 0) {
            return null;
        }
        return decoder.decode(element);
    }

    @Override
    public void skip() {
        queue.skip();
    }

    @Override
    public Peeks<E> peek(int maxElements) {
        return new ElementPeeks<>(decoder, queue.peek(maxElements));
    }

    @Override
    public void skip(Peeks<?> peeks) {
        queue.skip(peeks);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    public void close() {
        queue.close();
    }
}
