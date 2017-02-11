package npetzall.queue;

import npetzall.queue.api.Transcoder;
import npetzall.queue.file.QueueFileHandler;
import npetzall.queue.bytearray.ByteArrayQueue;
import npetzall.queue.helpers.SizeHelper;

import java.io.File;
import java.io.IOException;

public class PersistentQueueBuilder<E> {

    private File queueFile;
    private int size;
    private Transcoder<E> transcoder;

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

    public PersistentQueue<E> build() throws IOException {
        validate();
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size);
        return new PersistentQueue<>(new ByteArrayQueue(queueFileHandler, queueFileHandler), transcoder, transcoder);
    }

    private void validate() {
        //TODO: Add validation before creation
        //Not yet implemented
    }

}
