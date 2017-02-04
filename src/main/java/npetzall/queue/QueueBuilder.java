package npetzall.queue;

import npetzall.queue.api.Queue;
import npetzall.queue.api.QueueFactory;
import npetzall.queue.api.Transcoder;
import npetzall.queue.cache.OffHeapReadCache;
import npetzall.queue.cache.OnHeapReadCache;
import npetzall.queue.file.FileQueue;
import npetzall.queue.file.QueueFileHandler;
import npetzall.queue.helpers.SizeHelper;

import java.io.File;
import java.io.IOException;

public class QueueBuilder<E> {

    private File queueFile;
    private int size;
    private Transcoder<E> transcoder;
    private QueueFactory<byte[]> readCacheQueueFactory = null;

    private QueueBuilder(File queueFile) {
        this.queueFile = queueFile;
    }

    public static <E> QueueBuilder<E> queueFile(File queueFile) {
        return new QueueBuilder<>(queueFile);
    }

    public QueueBuilder<E> size(String size) {
        this.size = SizeHelper.parse(size);
        return this;
    }

    public QueueBuilder<E> transcoder(Transcoder<E> transcoder) {
        this.transcoder = transcoder;
        return this;
    }

    public QueueBuilder<E> offHeapReadCache() {
        readCacheQueueFactory = fileQueue -> new OffHeapReadCache(fileQueue);
        return this;
    }

    public QueueBuilder<E> onHeapReadCache() {
        readCacheQueueFactory = fileQueue -> new OnHeapReadCache(fileQueue);
        return this;
    }

    public Queue<E> build() throws IOException {
        validate();
        FileQueue fileQueue = new FileQueue(new QueueFileHandler(queueFile, size));
        return new PersistentQueue<>(fileQueue, transcoder, transcoder, readCacheQueueFactory);
    }

    private void validate() {
        //Not yet implemented
    }

}
