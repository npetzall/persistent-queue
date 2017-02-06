package npetzall.queue.cache;

import npetzall.queue.api.Peeks;
import npetzall.queue.api.Queue;
import npetzall.queue.file.FileQueue;

public class ReadThroughReadCache implements Queue<byte[]> {

    private final FileQueue fileQueue;

    public ReadThroughReadCache(FileQueue fileQueue) {
        this.fileQueue = fileQueue;
    }

    @Override
    public void enqueue(byte[] element) {
        fileQueue.enqueue(element);
    }

    @Override
    public byte[] dequeue() {
        return fileQueue.dequeue();
    }

    @Override
    public byte[] peek() {
        return fileQueue.peek();
    }

    @Override
    public void skip() {
        fileQueue.skip();
    }

    @Override
    public Peeks<byte[]> peek(int maxElements) {
        return fileQueue.peek(maxElements);
    }

    @Override
    public void skip(Peeks<?> peeks) {
        fileQueue.skip(peeks);
    }

    @Override
    public void clear() {
        fileQueue.clear();
    }

    @Override
    public void close() {
        fileQueue.close();
    }
}
