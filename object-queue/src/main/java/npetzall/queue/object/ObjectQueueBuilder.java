package npetzall.queue.object;

import npetzall.queue.bytearray.ByteArrayQueue;
import npetzall.queue.file.QueueFileHandler;
import npetzall.queue.helpers.SizeHelper;
import npetzall.queue.object.api.Transcoder;

import java.io.File;
import java.io.IOException;

public class ObjectQueueBuilder<E> {

    private File queueFile;
    private int size;
    private Transcoder<E> transcoder;

    private ObjectQueueBuilder(File queueFile) {
        this.queueFile = queueFile;
    }

    public static <E> ObjectQueueBuilder<E> queueFile(File queueFile) {
        return new ObjectQueueBuilder<>(queueFile);
    }

    public ObjectQueueBuilder<E> size(String size) {
        this.size = SizeHelper.parse(size);
        return this;
    }

    public ObjectQueueBuilder<E> transcoder(Transcoder<E> transcoder) {
        this.transcoder = transcoder;
        return this;
    }

    public ObjectQueue<E> build() throws IOException {
        validate();
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size);
        return new ObjectQueue<>(new ByteArrayQueue(queueFileHandler, queueFileHandler), transcoder, transcoder);
    }

    private void validate() {
        //TODO: Add validation before creation
        //Not yet implemented
    }

}
