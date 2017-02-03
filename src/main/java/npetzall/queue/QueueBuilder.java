package npetzall.queue;

import npetzall.queue.api.Queue;
import npetzall.queue.api.QueueFactory;
import npetzall.queue.api.Transcoder;
import npetzall.queue.cache.ReadCacheFactoryFactory;
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

    public static QueueBuilder queueFile(File queueFile) {
        return new QueueBuilder(queueFile);
    }

    public QueueBuilder size(String size) {
        this.size = SizeHelper.parse(size);
        return this;
    }

    public QueueBuilder transcoder(Transcoder<E> transcoder) {
        this.transcoder = transcoder;
        return this;
    }

    public QueueBuilder offHeapReadCache() {
        readCacheQueueFactory = ReadCacheFactoryFactory.offHeapFactory();
        return this;
    }

    public QueueBuilder onHeapReadCache() {
        readCacheQueueFactory = ReadCacheFactoryFactory.onHeapFactory();
        return this;
    }

    public Queue<E> build() throws IOException {
        validate();
        FileQueue fileQueue = new FileQueue(new QueueFileHandler(queueFile, size));
        return new PersistentQueue(fileQueue, transcoder, transcoder, readCacheQueueFactory);
    }

    private void validate() {
        //Not yet implemented
    }

}
