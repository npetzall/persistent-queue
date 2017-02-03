package npetzall.queue.file

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class FileQueueSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "should update writeOffset when element is enqueued"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        QueueFileHandler queueFileHandler = Mock {
            getDataByteBuffer() >> ByteBuffer.allocate(1024)
            getWriteOffset() >> 0
            getReadOffset() >> 0
        }
        FileQueue fileQueue = new FileQueue(queueFileHandler)

        when:
        fileQueue.enqueue(one)

        then:
        0 * queueFileHandler.setReadOffset(_)
        1 * queueFileHandler.setWriteOffset(_)

    }

    def "should update readOffset when element is dequeued"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        QueueFileHandler queueFileHandler = Mock {
            getDataByteBuffer() >> ByteBuffer.allocate(1024)
            getWriteOffset() >> 0
            getReadOffset() >> 0
        }
        FileQueue fileQueue = new FileQueue(queueFileHandler)
        fileQueue.enqueue(one)

        when:
        byte[] actualOne = fileQueue.dequeue()

        then:
        1 * queueFileHandler.setReadOffset(_)
        0 * queueFileHandler.setWriteOffset(_)
        assertThat(actualOne).isEqualTo(one)

    }

    def "should be able to return size of as reported by queueFileHandler"() {
        setup:
        QueueFileHandler queueFileHandler = Mock() {
            getDataByteBuffer() >> ByteBuffer.allocate(1024)
            getWriteOffset() >> 0
            getReadOffset() >> 0
            getSize() >> 1018
        }
        FileQueue fileQueue = new FileQueue(queueFileHandler)

        when:
        int size = fileQueue.getSize()

        then:
        size == 1018
    }

    def "should be able to skip elements"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        QueueFileHandler queueFileHandler = Mock {
            getDataByteBuffer() >> ByteBuffer.allocate(1024)
            getWriteOffset() >> 0
            getReadOffset() >> 0
        }
        FileQueue fileQueue = new FileQueue(queueFileHandler)
        fileQueue.enqueue(one)
        fileQueue.enqueue(two)

        when:
        fileQueue.skip()

        then:
        1 * queueFileHandler.setReadOffset(_)
        0 * queueFileHandler.setWriteOffset(_)
    }

    def "peek doesn't move readOffset"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        QueueFileHandler queueFileHandler = Mock {
            getDataByteBuffer() >> ByteBuffer.allocate(1024)
            getWriteOffset() >> 0
            getReadOffset() >> 0
        }
        FileQueue fileQueue = new FileQueue(queueFileHandler)
        fileQueue.enqueue(one)

        when:
        fileQueue.peek()

        then:
        0 * queueFileHandler.setWriteOffset(_)
        0 * queueFileHandler.setReadOffset(_)
    }

    def "should be able to clear all elements"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        QueueFileHandler queueFileHandler = Mock {
            getDataByteBuffer() >> ByteBuffer.allocate(1024)
            getWriteOffset() >> 0
            getReadOffset() >> 0
        }
        FileQueue fileQueue = new FileQueue(queueFileHandler)
        fileQueue.enqueue(one)

        when:
        fileQueue.clear()

        then:
        1 * queueFileHandler.setWriteOffset(0)
        1 * queueFileHandler.setReadOffset(0)
    }

}
