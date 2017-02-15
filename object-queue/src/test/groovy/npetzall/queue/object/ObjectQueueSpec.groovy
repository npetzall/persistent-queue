package npetzall.queue.object


import npetzall.queue.bytearray.ByteArrayQueue
import npetzall.queue.object.codec.StringTranscoder
import spock.lang.Specification

import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class ObjectQueueSpec extends Specification {

    def "should send bytes to underlying queue"() {
        setup:
        String oneStr = "one"
        byte[] oneBytes = oneStr.getBytes(StandardCharsets.UTF_8)
        ByteArrayQueue fileQueue = Mock {

        }
        ObjectQueue<String> persistentQueue = new ObjectQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

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
            deque() >> oneBytes
        }
        ObjectQueue<String> persistentQueue = new ObjectQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        String actualOneStr = persistentQueue.deque()

        then:
        assertThat(actualOneStr).isEqualToIgnoringCase(oneStr)
    }

    def "should return null when de-queueing returns empty byteArray from underlying queue"() {
        setup:
        ByteArrayQueue fileQueue = Mock {
            deque() >> new byte[0];
        }
        ObjectQueue<String> persistentQueue = new ObjectQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        String actualOneStr = persistentQueue.deque()

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
        ObjectQueue<String> persistentQueue = new ObjectQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

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
        ObjectQueue<String> persistentQueue = new ObjectQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        String actualOneStr = persistentQueue.peek()

        then:
        assertThat(actualOneStr).isNull();
    }

    def "should call clear on readCacheQueue when clear is called"() {
        setup:
        ByteArrayQueue fileQueue = Mock {

        }
        ObjectQueue<String> persistentQueue = new ObjectQueue<>(fileQueue, new StringTranscoder(), new StringTranscoder())

        when:
        persistentQueue.clear()

        then:
        1 * fileQueue.clear()
    }
}
