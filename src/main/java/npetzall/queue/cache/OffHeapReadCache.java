package npetzall.queue.cache;

import npetzall.queue.file.FileQueue;

import java.nio.ByteBuffer;

public class OffHeapReadCache extends ReadCache{

    public OffHeapReadCache(FileQueue fileQueue) {
        super(fileQueue,fileQueue.copyTo(ByteBuffer.allocateDirect(fileQueue.getSize())));
    }
}
