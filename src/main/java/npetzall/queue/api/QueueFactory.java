package npetzall.queue.api;

import npetzall.queue.file.FileQueue;

public interface QueueFactory<E> {
    Queue<E> create(FileQueue fileQueue);
}
