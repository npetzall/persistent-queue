package npetzall.queue.bytebuffer

import npetzall.queue.api.CapacityMismatchException
import npetzall.queue.api.NoSpaceLeftRuntimeException
import npetzall.queue.peek.DataPeeks
import spock.lang.Specification

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class ByteBufferQueueSpec extends Specification {

    def "Can create a ByteBufferQueue"() {
        when:
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))

        then:
        byteBufferQueue != null
    }

    def "Writing a value moves the writeIndex"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))

        when:
        int preWriteIndex = byteBufferQueue.getWriteIndex()

        and:
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)

        and:
        int postWriteIndex = byteBufferQueue.getWriteIndex();

        then:
        assertThat(preWriteIndex).isLessThan(postWriteIndex)
        assertThat(postWriteIndex).isEqualTo(one.length+4+two.length+4)
    }

    def "Reading a value moves the readIndex"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)

        when:
        int preReadIndex = byteBufferQueue.getReadIndex()

        and:
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()

        and:
        int postReadIndex = byteBufferQueue.getReadIndex()

        then:
        assertThat(preReadIndex).isLessThan(postReadIndex)
        assertThat(postReadIndex).isEqualTo(one.length+4+two.length+4)
    }

    def "can't read beyond the writeIndex"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))
        byteBufferQueue.enqueue(one)

        when:
        byte[] actualOne = byteBufferQueue.dequeue()

        and:
        byte[] firstTwo = byteBufferQueue.dequeue()

        and:
        byteBufferQueue.enqueue(two)
        byte[] actualTwo = byteBufferQueue.dequeue()

        then:
        assertThat(actualOne).containsExactly(one)
        assertThat(firstTwo).isEmpty()
        assertThat(actualTwo).containsExactly(two)
    }

    def "When writing and element doesn't fit at end of buffer it's written in the beginning when it fits" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(20))
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()

        when:
        byteBufferQueue.enqueue(three)
        int diff = byteBufferQueue.getWriteIndex() - byteBufferQueue.getReadIndex()

        and:
        byte[] actualThree = byteBufferQueue.dequeue()

        then:
        assertThat(diff).isNegative()
        assertThat(actualThree).containsExactly(three)
    }

    def "When reading the element after the last in the end, it reads the next which is in the beginning" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(20))
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()
        byteBufferQueue.enqueue(three)

        when:
        byte[] actualThree = byteBufferQueue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)
    }

    def "Can't write beyond the readIndex"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(23))
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.enqueue(three)

        when:
        byteBufferQueue.enqueue(four)

        then:
        thrown(NoSpaceLeftRuntimeException)
    }

    def "Peek doesn't remove element"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(23))
        byteBufferQueue.enqueue(one)

        when:
        byte[] peekOne = byteBufferQueue.peek()

        then:
        assertThat(peekOne).containsExactly(one)

        when:
        byte[] actualOne = byteBufferQueue.dequeue()

        then:
        assertThat(actualOne).containsExactly(one)

        when:
        byte[] empty = byteBufferQueue.dequeue()

        then:
        assertThat(empty).isEmpty()

    }

    def "Peek around the end" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(20))
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()
        byteBufferQueue.enqueue(three)

        when:
        byte[] peekThree = byteBufferQueue.peek()

        then:
        assertThat(peekThree).containsExactly(three)

        when:
        byte[] actualThree = byteBufferQueue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)

        when:
        byte[] empty = byteBufferQueue.dequeue()

        then:
        assertThat(empty).isEmpty()
    }

    def "Clear the ByteBufferQueue" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(23))
        byteBufferQueue.enqueue(one)

        when:
        byte[] peekOne = byteBufferQueue.peek()

        then:
        assertThat(peekOne).containsExactly(one);

        when:
        byteBufferQueue.clear()

        and:
        peekOne = byteBufferQueue.peek()
        byte[] actualOne = byteBufferQueue.dequeue();

        then:
        assertThat(peekOne).isEmpty()
        assertThat(actualOne).isEmpty()
    }

    def "Clear the ByteBufferQueue after i has started to recycle space"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(20))
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()
        byteBufferQueue.enqueue(three)

        when:
        byte[] peekThree = byteBufferQueue.peek()

        then:
        assertThat(peekThree).containsExactly(three)

        when:
        byteBufferQueue.clear()

        and:
        byte[] peek = byteBufferQueue.peek()
        byte[] dequeued = byteBufferQueue.dequeue()

        then:
        assertThat(peek).isEmpty()
        assertThat(dequeued).isEmpty()

        when:
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)

        then:
        noExceptionThrown()
    }

    def "Recycle with recycle marker" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(20))

        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()

        when:
        byteBufferQueue.enqueue(three)
        byte[] actualThree = byteBufferQueue.dequeue()

        and:
        byteBufferQueue.enqueue(four)
        byte[] actualFour = byteBufferQueue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)
        assertThat(actualFour).containsExactly(four)
    }

    def "Recycle withour recycle marker" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(23))

        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()

        when:
        byteBufferQueue.enqueue(three)
        byte[] actualThree = byteBufferQueue.dequeue()

        and:
        byteBufferQueue.enqueue(four)
        byte[] actualFour = byteBufferQueue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)
        assertThat(actualFour).containsExactly(four)
    }

    def "Skip will advance the reader by 1 element" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(23))
        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)

        when:
        byteBufferQueue.skip()

        and:
        byte[] actualTwo = byteBufferQueue.dequeue()

        then:
        assertThat(actualTwo).containsExactly(two)
    }

    def "Skip works at end of buffer with recycle marker" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(20))

        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()

        when:
        byteBufferQueue.enqueue(three)
        byteBufferQueue.skip()

        and:
        byteBufferQueue.enqueue(four)
        byte[] actualFour = byteBufferQueue.dequeue()

        then:
        assertThat(actualFour).containsExactly(four)
    }

    def "Skip works at end of buffer without recycle marker" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(23))

        byteBufferQueue.enqueue(one)
        byteBufferQueue.enqueue(two)
        byteBufferQueue.dequeue()
        byteBufferQueue.dequeue()

        when:
        byteBufferQueue.enqueue(three)
        byteBufferQueue.skip()

        and:
        byteBufferQueue.enqueue(four)
        byte[] actualFour = byteBufferQueue.dequeue()

        then:
        assertThat(actualFour).containsExactly(four)
    }

    def "should be able to peek a number of element" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))
        byteBufferQueue.enqueue(one);
        byteBufferQueue.enqueue(two);
        byteBufferQueue.enqueue(three);
        byteBufferQueue.enqueue(four);

        when:
        DataPeeks peeks = byteBufferQueue.peek(3)

        and:
        byte[] actualOne = byteBufferQueue.dequeue();

        then:
        assertThat(peeks).containsExactly(one, two, three)
        assertThat(actualOne).containsExactly(one)
    }

    def "Should be able to use a Peeks to skip all of them" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))
        byteBufferQueue.enqueue(one);
        byteBufferQueue.enqueue(two);
        byteBufferQueue.enqueue(three);
        byteBufferQueue.enqueue(four);
        DataPeeks peeks = byteBufferQueue.peek(3)

        when:
        byteBufferQueue.skip(peeks)

        and:
        byte[] actualFour = byteBufferQueue.dequeue()

        then:
        assertThat(actualFour).containsExactly(four);
    }

    def "Should be able to use Peeks to skip even when things have been dequeued" () {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        byte[] four = "four".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))
        byteBufferQueue.enqueue(one);
        byteBufferQueue.enqueue(two);
        byteBufferQueue.enqueue(three);
        byteBufferQueue.enqueue(four);
        DataPeeks peeks = byteBufferQueue.peek(3)

        when:
        byte[] actualOne = byteBufferQueue.dequeue()
        and:
        byteBufferQueue.skip(peeks)

        and:
        byte[] actualFour = byteBufferQueue.dequeue()

        then:
        assertThat(actualOne).containsExactly(one)
        assertThat(actualFour).containsExactly(four)
    }

    def "Copy byteBufferQueue to another byteBufferQueue"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))
        byteBufferQueue.enqueue(one);
        byteBufferQueue.enqueue(two);
        byteBufferQueue.enqueue(three);
        byteBufferQueue.dequeue()

        when:
        ByteBufferQueue byteBufferQueueCopy = byteBufferQueue.copyTo(ByteBuffer.allocate(1024))

        and:
        byte[] actualTwo = byteBufferQueue.dequeue()
        byte[] actualTwoCopy = byteBufferQueueCopy.dequeue()

        then:
        with(byteBufferQueueCopy) {
            getReadIndex() == byteBufferQueue.getReadIndex()
            getWriteIndex() == byteBufferQueue.getWriteIndex()
        }
        assertThat(actualTwo).containsExactly(two)
        assertThat(actualTwoCopy).containsExactly(two)
    }

    def "should throw exception if capacity mismatch"() {
        setup:
        byte[] one = "one".getBytes(StandardCharsets.UTF_8)
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        byte[] three = "three".getBytes(StandardCharsets.UTF_8)
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(1024))
        byteBufferQueue.enqueue(one);
        byteBufferQueue.enqueue(two);
        byteBufferQueue.enqueue(three);
        byteBufferQueue.dequeue()

        when:
        byteBufferQueue.copyTo(ByteBuffer.allocate(124))

        then:
        thrown(CapacityMismatchException)
    }
}
