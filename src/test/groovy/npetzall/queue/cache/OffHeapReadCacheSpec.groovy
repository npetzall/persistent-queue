package npetzall.queue.cache

import npetzall.queue.bytebuffer.ByteBufferQueue
import npetzall.queue.file.FileQueue
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class OffHeapReadCacheSpec extends Specification {

    def "Off heap is created with a direct byteBuffer"() {
        FileQueue fileQueue = Mock {
            copyTo(_) >> null
        }

        when:
        new OffHeapReadCache(fileQueue)

        then:
        1 * fileQueue.copyTo({it.isDirect() == true})
    }

    def "Enqueue calls enqueue on cache and filequeue"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = Mock(ByteBufferQueue)
        FileQueue fileQueue = Mock {
            copyTo(_) >> byteBufferQueue
        }
        OffHeapReadCache offHeapReadCache = new OffHeapReadCache(fileQueue)

        when:
        offHeapReadCache.enqueue(one)

        then:
        1 * byteBufferQueue.enqueue(one)
        1 * fileQueue.enqueue(one)
    }

    def "Dequeue calls dequeue on cache and skip on fileQueue"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = Mock() {
            dequeue() >> one
        }
        FileQueue fileQueue = Mock {
            copyTo(_) >> byteBufferQueue
        }
        OffHeapReadCache offHeapReadCache = new OffHeapReadCache(fileQueue)

        when:
        offHeapReadCache.dequeue()

        then:
        1 * byteBufferQueue.dequeue()
        1 * fileQueue.skip()
        0 * fileQueue.dequeue()
    }

    def "Peek only touches the cache"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = Mock() {
            peek() >> one
        }
        FileQueue fileQueue = Mock {
            copyTo(_) >> byteBufferQueue
        }
        OffHeapReadCache offHeapReadCache = new OffHeapReadCache(fileQueue)

        when:
        offHeapReadCache.peek()

        then:
        1 * byteBufferQueue.peek()
        0 * fileQueue.peek()
    }

    def "Clear clears filequeue, bytebufferqueue"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = Mock() {
            peek() >> one
        }
        FileQueue fileQueue = Mock {
            copyTo(_) >> byteBufferQueue
        }
        OffHeapReadCache offHeapReadCache = new OffHeapReadCache(fileQueue)

        when:
        offHeapReadCache.clear()

        then:
        1 * byteBufferQueue.clear()
        1 * fileQueue.clear()
    }
}
