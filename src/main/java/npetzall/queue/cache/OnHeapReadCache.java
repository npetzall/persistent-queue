package npetzall.queue.cache;

import npetzall.queue.file.FileQueue;

import java.nio.ByteBuffer;

public class OnHeapReadCache extends ReadCache{

    public OnHeapReadCache(FileQueue fileQueue) {
        super(fileQueue,fileQueue.copyTo(ByteBuffer.allocate(fileQueue.getSize())));
    }
}
