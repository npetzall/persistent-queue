package npetzall.queue

import npetzall.queue.api.QueueFactory
import npetzall.queue.bytebuffer.ByteBufferQueue
import npetzall.queue.codec.StringTranscoder
import npetzall.queue.file.FileQueue
import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class PersistentQueueSpec extends Specification {

    def "should send bytes to underlying queue"() {
        setup:
        String oneStr = "one"
        byte[] oneBytes = oneStr.getBytes(StandardCharsets.UTF_8)
        FileQueue fileQueue = Mock {

        }
        ByteBufferQueue byteBufferQueue = Mock {

        }
        QueueFactory queueFactory = Mock {
            create(_) >> byteBufferQueue
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder(), queueFactory)

        when:
        persistentQueue.enqueue(oneStr)

        then:
        1 * byteBufferQueue.enqueue(oneBytes)

    }

    def "should return string when de-queing from underlying queue"() {
        setup:
        String oneStr = "one"
        byte[] oneBytes = oneStr.getBytes(StandardCharsets.UTF_8)
        FileQueue fileQueue = Mock {

        }
        ByteBufferQueue byteBufferQueue = Mock {
            dequeue() >> oneBytes
        }
        QueueFactory queueFactory = Mock {
            create(_) >> byteBufferQueue
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder(), queueFactory)

        when:
        String actualOneStr = persistentQueue.dequeue()

        then:
        assertThat(actualOneStr).isEqualToIgnoringCase(oneStr)
    }

    def "should return null when de-queueing returns empty byteArray from underlying queue"() {
        setup:
        FileQueue fileQueue = Mock {

        }
        ByteBufferQueue byteBufferQueue = Mock {
            dequeue() >> new byte[0];
        }
        QueueFactory queueFactory = Mock {
            create(_) >> byteBufferQueue
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder(), queueFactory)

        when:
        String actualOneStr = persistentQueue.dequeue()

        then:
        assertThat(actualOneStr).isNull();
    }

    def "should return string when peeking"() {
        setup:
        String oneStr = "one"
        byte[] oneBytes = oneStr.getBytes(StandardCharsets.UTF_8)
        FileQueue fileQueue = Mock {

        }
        ByteBufferQueue byteBufferQueue = Mock {
            peek() >> oneBytes
        }
        QueueFactory queueFactory = Mock {
            create(_) >> byteBufferQueue
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder(), queueFactory)

        when:
        String actualOneStr = persistentQueue.peek()

        then:
        assertThat(actualOneStr).isEqualToIgnoringCase(oneStr)
    }

    def "should return null if underlying returns empty byteArray from underlying queue"() {
        setup:
        FileQueue fileQueue = Mock {

        }
        ByteBufferQueue byteBufferQueue = Mock {
            peek() >> new byte[0];
        }
        QueueFactory queueFactory = Mock {
            create(_) >> byteBufferQueue
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder(), queueFactory)

        when:
        String actualOneStr = persistentQueue.peek()

        then:
        assertThat(actualOneStr).isNull();
    }

    def "should call clear on readCacheQueue when clear is called"() {
        setup:
        FileQueue fileQueue = Mock {

        }
        ByteBufferQueue byteBufferQueue = Mock {

        }
        QueueFactory queueFactory = Mock {
            create(_) >> byteBufferQueue
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder(), queueFactory)

        when:
        persistentQueue.clear()

        then:
        1 * byteBufferQueue.clear()
    }
}
