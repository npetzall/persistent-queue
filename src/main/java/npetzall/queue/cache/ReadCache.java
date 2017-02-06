package npetzall.queue.cache;

import npetzall.queue.api.Peeks;
import npetzall.queue.api.Queue;
import npetzall.queue.bytebuffer.ByteBufferQueue;
import npetzall.queue.file.FileQueue;

public abstract class ReadCache implements Queue<byte[]> {

    protected final FileQueue fileQueue;
    protected final ByteBufferQueue byteBufferQueue;

    public ReadCache(FileQueue fileQueue, ByteBufferQueue byteBufferQueue) {
        this.fileQueue = fileQueue;
        this.byteBufferQueue = byteBufferQueue;
    }

    @Override
    public void enqueue(byte[] element) {
        byteBufferQueue.enqueue(element);
        fileQueue.enqueue(element);
    }

    @Override
    public byte[] dequeue() {
        byte[] element = byteBufferQueue.dequeue();
        fileQueue.skip();
        return element;
    }

    @Override
    public byte[] peek() {
        return byteBufferQueue.peek();
    }

    @Override
    public void skip() {
        byteBufferQueue.skip();
        fileQueue.skip();
    }

    @Override
    public Peeks<byte[]> peek(int maxElements) {
        return byteBufferQueue.peek(maxElements);
    }

    @Override
    public void skip(Peeks<?> peeks) {
        byteBufferQueue.skip(peeks);
        fileQueue.skip(peeks);
    }

    @Override
    public void clear() {
        byteBufferQueue.clear();
        fileQueue.clear();
    }

    @Override
    public void close() {
        fileQueue.close();
    }
}
