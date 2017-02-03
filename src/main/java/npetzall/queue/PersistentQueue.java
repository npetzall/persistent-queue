package npetzall.queue;

import npetzall.queue.api.*;
import npetzall.queue.cache.ReadThroughReadCache;
import npetzall.queue.file.FileQueue;
import npetzall.queue.peek.ElementPeeks;

import java.io.IOException;

public class PersistentQueue<E> implements Queue<E> {

    private final Encoder<E> encoder;
    private final Decoder<E> decoder;
    private final Queue<byte[]> readCacheQueue;

    public PersistentQueue(FileQueue fileQueue, Encoder<E> encoder, Decoder<E> decoder, QueueFactory<byte[]> readCacheQueue) throws IOException {
        this.encoder = encoder;
        this.decoder = decoder;
        this.readCacheQueue = readCacheQueue != null ? readCacheQueue.create(fileQueue) : new ReadThroughReadCache(fileQueue);
    }

    @Override
    public void enqueue(E element) {
        byte[] elementBytes = encoder.encode(element);
        if (elementBytes.length > 0) {
            readCacheQueue.enqueue(elementBytes);
        }
    }

    @Override
    public E dequeue() {
        byte[] element = readCacheQueue.dequeue();
        if (element.length == 0) {
            return null;
        }
        return decoder.decode(element);
    }

    @Override
    public E peek() {
        byte[] element = readCacheQueue.peek();
        if (element.length == 0) {
            return null;
        }
        return decoder.decode(element);
    }

    @Override
    public void skip() {
        readCacheQueue.skip();
    }

    @Override
    public Peeks<E> peek(int maxElements) {
        return new ElementPeeks<>(decoder, readCacheQueue.peek(maxElements));
    }

    @Override
    public void skip(Peeks peeks) {
        readCacheQueue.skip(peeks);
    }

    @Override
    public void clear() {
        readCacheQueue.clear();
    }

    public Class<?> getReadCacheClass() {
        return readCacheQueue.getClass();
    }
}
