package npetzall.queue;

import npetzall.queue.api.QueueFactory;
import npetzall.queue.api.Transcoder;
import npetzall.queue.cache.OffHeapReadCache;
import npetzall.queue.cache.OnHeapReadCache;
import npetzall.queue.file.FileQueue;
import npetzall.queue.file.QueueFileHandler;
import npetzall.queue.helpers.SizeHelper;

import java.io.File;
import java.io.IOException;

public class PersistentQueueBuilder<E> {

    private File queueFile;
    private int size;
    private Transcoder<E> transcoder;
    private QueueFactory<byte[]> readCacheQueueFactory = null;

    private PersistentQueueBuilder(File queueFile) {
        this.queueFile = queueFile;
    }

    public static <E> PersistentQueueBuilder<E> queueFile(File queueFile) {
        return new PersistentQueueBuilder<>(queueFile);
    }

    public PersistentQueueBuilder<E> size(String size) {
        this.size = SizeHelper.parse(size);
        return this;
    }

    public PersistentQueueBuilder<E> transcoder(Transcoder<E> transcoder) {
        this.transcoder = transcoder;
        return this;
    }

    public PersistentQueueBuilder<E> offHeapReadCache() {
        readCacheQueueFactory = OffHeapReadCache::new;
        return this;
    }

    public PersistentQueueBuilder<E> onHeapReadCache() {
        readCacheQueueFactory = OnHeapReadCache::new;
        return this;
    }

    public PersistentQueue<E> build() throws IOException {
        validate();
        FileQueue fileQueue = new FileQueue(new QueueFileHandler(queueFile, size));
        return new PersistentQueue<>(fileQueue, transcoder, transcoder, readCacheQueueFactory);
    }

    private void validate() {
        //TODO: Add validation before creation
        //Not yet implemented
    }

}
