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
                queueFileHandler.getReadOffset(),
                queueFileHandler.getQueueLength());
    }

    public int getSize() {
        return queueFileHandler.getSize();
    }

    @Override
    public boolean enqueue(byte[] element) {
        if (byteBufferQueue.enqueue(element)) {
            queueFileHandler.setWriteOffset(byteBufferQueue.getWriteIndex());
            queueFileHandler.setQueueLength(byteBufferQueue.queueLength());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public byte[] dequeue() {
        byte[] element = byteBufferQueue.dequeue();
        queueFileHandler.setReadOffset(byteBufferQueue.getReadIndex());
        queueFileHandler.setQueueLength(byteBufferQueue.queueLength());
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
        queueFileHandler.setQueueLength(byteBufferQueue.queueLength());
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
        queueFileHandler.setQueueLength(byteBufferQueue.queueLength());
    }

    public ByteBufferQueue copyTo(ByteBuffer byteBuffer) {
        return byteBufferQueue.copyTo(byteBuffer);
    }

    @Override
    public int queueLength() {
        return byteBufferQueue.queueLength();
    }

    @Override
    public void close() {
        queueFileHandler.close();
    }
}
