package npetzall.queue


import npetzall.queue.bytearray.ByteArrayQueue
import npetzall.queue.codec.StringTranscoder
import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class PersistentQueueSpec extends Specification {

    def "should send bytes to underlying queue"() {
        setup:
        String oneStr = "one"
        byte[] oneBytes = oneStr.getBytes(StandardCharsets.UTF_8)
        ByteArrayQueue fileQueue = Mock {

        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        persistentQueue.enqueue(oneStr)

        then:
        1 * fileQueue.enqueue(oneBytes)

    }

    def "should return string when de-queing from underlying queue"() {
        setup:
        String oneStr = "one"
        byte[] oneBytes = oneStr.getBytes(StandardCharsets.UTF_8)
        ByteArrayQueue fileQueue = Mock {
            dequeue() >> oneBytes
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        String actualOneStr = persistentQueue.dequeue()

        then:
        assertThat(actualOneStr).isEqualToIgnoringCase(oneStr)
    }

    def "should return null when de-queueing returns empty byteArray from underlying queue"() {
        setup:
        ByteArrayQueue fileQueue = Mock {
            dequeue() >> new byte[0];
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        String actualOneStr = persistentQueue.dequeue()

        then:
        assertThat(actualOneStr).isNull();
    }

    def "should return string when peeking"() {
        setup:
        String oneStr = "one"
        byte[] oneBytes = oneStr.getBytes(StandardCharsets.UTF_8)
        ByteArrayQueue fileQueue = Mock {
            peek() >> oneBytes
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        String actualOneStr = persistentQueue.peek()

        then:
        assertThat(actualOneStr).isEqualToIgnoringCase(oneStr)
    }

    def "should return null if underlying returns empty byteArray from underlying queue"() {
        setup:
        ByteArrayQueue fileQueue = Mock {
            peek() >> new byte[0];
        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        String actualOneStr = persistentQueue.peek()

        then:
        assertThat(actualOneStr).isNull();
    }

    def "should call clear on readCacheQueue when clear is called"() {
        setup:
        ByteArrayQueue fileQueue = Mock {

        }
        PersistentQueue<String> persistentQueue = new PersistentQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        persistentQueue.clear()

        then:
        1 * fileQueue.clear()
    }
}
