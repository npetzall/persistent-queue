package npetzall.queue.cache

import npetzall.queue.file.FileQueue
import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class ReadThroughReadCacheSpec extends Specification {

    @Shared byte[] one = "one".getBytes(StandardCharsets.UTF_8);

    def "should call enqueue on fileQueue when enqueuing"() {
        setup:
        FileQueue fileQueue = Mock(FileQueue)
        ReadThroughReadCache readThroughReadCache = new ReadThroughReadCache(fileQueue)

        when:
        readThroughReadCache.enqueue(one)

        then:
        1 * fileQueue.enqueue(one)
    }

    def "should call dequeue on fileQueue when de-queuing"() {
        setup:
        FileQueue fileQueue = Mock(FileQueue)
        ReadThroughReadCache readThroughReadCache = new ReadThroughReadCache(fileQueue)

        when:
        readThroughReadCache.dequeue()

        then:
        1 * fileQueue.dequeue()
    }

    def "should call peek on fileQueue when peeking"() {
        setup:
        FileQueue fileQueue = Mock(FileQueue)
        ReadThroughReadCache readThroughReadCache = new ReadThroughReadCache(fileQueue)

        when:
        readThroughReadCache.peek()

        then:
        1 * fileQueue.peek()
    }

    def "should call clear on fileQueue when clearing"() {
        setup:
        FileQueue fileQueue = Mock(FileQueue)
        ReadThroughReadCache readThroughReadCache = new ReadThroughReadCache(fileQueue)

        when:
        readThroughReadCache.clear()

        then:
        1 * fileQueue.clear()
    }
}
