package npetzall.queue.file;

import npetzall.queue.api.Peeks;
import npetzall.queue.api.Queue;
import npetzall.queue.bytebuffer.ByteBufferQueue;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FileQueue implements Queue<byte[]> {

    protected final QueueFileHandler queueFileHandler;
    protected final ByteBufferQueue byteBufferQueue;

    public FileQueue(QueueFileHandler queueFileHandler) throws IOException {
        this.queueFileHandler = queueFileHandler;
        byteBufferQueue = new ByteBufferQueue(
                queueFileHandler.getDataByteBuffer(),
                queueFileHandler.getWriteOffset(),
                queueFileHandler.getReadOffset());
    }

    public int getSize() {
        return queueFileHandler.getSize();
    }

    @Override
    public void enqueue(byte[] element) {
        byteBufferQueue.enqueue(element);
        queueFileHandler.setWriteOffset(byteBufferQueue.getWriteIndex());
    }

    @Override
    public byte[] dequeue() {
        byte[] element = byteBufferQueue.dequeue();
        queueFileHandler.setReadOffset(byteBufferQueue.getReadIndex());
        return element;
    }

    @Override
    public byte[] peek() {
        return byteBufferQueue.peek();
    }

    @Override
    public void skip() {
        byteBufferQueue.skip();
        queueFileHandler.setReadOffset(byteBufferQueue.getReadIndex());
    }

    @Override
    public Peeks<byte[]> peek(int maxElements) {
        return byteBufferQueue.peek(maxElements);
    }

    @Override
    public void skip(Peeks<?> peeks) {
        byteBufferQueue.skip(peeks);
    }

    @Override
    public void clear() {
        byteBufferQueue.clear();
        queueFileHandler.setWriteOffset(byteBufferQueue.getWriteIndex());
        queueFileHandler.setReadOffset(byteBufferQueue.getReadIndex());
    }

    public ByteBufferQueue copyTo(ByteBuffer byteBuffer) {
        return byteBufferQueue.copyTo(byteBuffer);
    }

    @Override
    public void close() {
        queueFileHandler.close();
    }
}
