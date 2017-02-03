package npetzall.queue.cache;

import npetzall.queue.api.Queue;
import npetzall.queue.api.QueueFactory;
import npetzall.queue.file.FileQueue;

public class ReadCacheFactoryFactory {

    private ReadCacheFactoryFactory() {}

    public static QueueFactory<byte[]> offHeapFactory() {
        return new QueueFactory() {
            @Override
            public Queue create(FileQueue fileQueue) {
                return new OffHeapReadCache(fileQueue);
            }
        };
    }

    public static QueueFactory<byte[]> onHeapFactory() {
        return new QueueFactory() {
            @Override
            public Queue create(FileQueue fileQueue) {
                return new OnHeapReadCache(fileQueue);
            }
        };
    }
}
