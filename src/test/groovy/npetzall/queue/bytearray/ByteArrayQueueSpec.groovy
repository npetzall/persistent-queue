package npetzall.queue.bytearray

import npetzall.queue.bytearray.ByteArrayQueue
import npetzall.queue.doubles.ByteBufferProviderDouble
import npetzall.queue.doubles.PositionHolderDouble
import npetzall.queue.peek.DataPeeks
import spock.lang.Shared
import spock.lang.Specification

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class ByteArrayQueueSpec extends Specification {
    
    @Shared byte[] one = "one".getBytes(StandardCharsets.UTF_8);
    @Shared byte[] two = "two".getBytes(StandardCharsets.UTF_8);
    @Shared byte[] three = "three".getBytes(StandardCharsets.UTF_8);
    @Shared byte[] four = "four".getBytes(StandardCharsets.UTF_8);

    def "Can create a ByteBufferQueue"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        when:
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)

        then:
        queue != null
    }

    def "can't read beyond the writeIndex"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)

        when:
        byte[] actualOne = queue.dequeue()

        and:
        byte[] firstTwo = queue.dequeue()

        and:
        queue.enqueue(two)
        byte[] actualTwo = queue.dequeue()

        then:
        assertThat(actualOne).containsExactly(one)
        assertThat(firstTwo).isEmpty()
        assertThat(actualTwo).containsExactly(two)
    }

    def "When writing and element doesn't fit at end of buffer it's written in the beginning when it fits" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(20))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()

        when:
        queue.enqueue(three)

        and:
        byte[] actualThree = queue.dequeue()

        then:
        assertThat(byteBufferProviderDouble.byteBuffer().capacity()).isLessThan(storageSize(one,two,three))
        assertThat(actualThree).containsExactly(three)
    }

    private int storageSize(byte[]...elements) {
        int sum = 0;
        elements.each {
            sum += it.length + 4
        }
        return sum
    }

    def "When reading the element after the last in the end, it reads the next which is in the beginning" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(20))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()
        queue.enqueue(three)

        when:
        byte[] actualThree = queue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)
    }

    def "Can't write beyond the readIndex"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(23))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.enqueue(three)

        when:
        boolean enqueued = queue.enqueue(four)

        then:
        assertThat(enqueued).isFalse()
    }

    def "Peek doesn't remove element"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(23))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)

        when:
        byte[] peekOne = queue.peek()

        then:
        assertThat(peekOne).containsExactly(one)

        when:
        byte[] actualOne = queue.dequeue()

        then:
        assertThat(actualOne).containsExactly(one)

        when:
        byte[] empty = queue.dequeue()

        then:
        assertThat(empty).isEmpty()

    }

    def "Peek around the end" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(20))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()
        queue.enqueue(three)

        when:
        byte[] peekThree = queue.peek()

        then:
        assertThat(peekThree).containsExactly(three)

        when:
        byte[] actualThree = queue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)

        when:
        byte[] empty = queue.dequeue()

        then:
        assertThat(empty).isEmpty()
    }

    def "Clear the ByteBufferQueue" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(23))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)

        when:
        byte[] peekOne = queue.peek()

        then:
        assertThat(peekOne).containsExactly(one);

        when:
        queue.clear()

        and:
        peekOne = queue.peek()
        byte[] actualOne = queue.dequeue();

        then:
        assertThat(peekOne).isEmpty()
        assertThat(actualOne).isEmpty()
    }

    def "Clear the ByteBufferQueue after i has started to recycle space"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(20))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()
        queue.enqueue(three)

        when:
        byte[] peekThree = queue.peek()

        then:
        assertThat(peekThree).containsExactly(three)

        when:
        queue.clear()

        and:
        byte[] peek = queue.peek()
        byte[] dequeued = queue.dequeue()

        then:
        assertThat(peek).isEmpty()
        assertThat(dequeued).isEmpty()

        when:
        queue.enqueue(one)
        queue.enqueue(two)

        then:
        noExceptionThrown()
    }

    def "Recycle with recycle marker" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(20))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)

        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()

        when:
        queue.enqueue(three)
        byte[] actualThree = queue.dequeue()

        and:
        queue.enqueue(four)
        byte[] actualFour = queue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)
        assertThat(actualFour).containsExactly(four)
    }

    def "Recycle withour recycle marker" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(23))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)

        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()

        when:
        queue.enqueue(three)
        byte[] actualThree = queue.dequeue()

        and:
        queue.enqueue(four)
        byte[] actualFour = queue.dequeue()

        then:
        assertThat(actualThree).containsExactly(three)
        assertThat(actualFour).containsExactly(four)
    }

    def "Skip will advance the reader by 1 element" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(23))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one)
        queue.enqueue(two)

        when:
        queue.skip()

        and:
        byte[] actualTwo = queue.dequeue()

        then:
        assertThat(actualTwo).containsExactly(two)
    }

    def "Skip works at end of buffer with recycle marker" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(20))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)

        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()

        when:
        queue.enqueue(three)
        queue.skip()

        and:
        queue.enqueue(four)
        byte[] actualFour = queue.dequeue()

        then:
        assertThat(actualFour).containsExactly(four)
    }

    def "Skip works at end of buffer without recycle marker" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(23))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)

        queue.enqueue(one)
        queue.enqueue(two)
        queue.dequeue()
        queue.dequeue()

        when:
        queue.enqueue(three)
        queue.skip()

        and:
        queue.enqueue(four)
        byte[] actualFour = queue.dequeue()

        then:
        assertThat(actualFour).containsExactly(four)
    }

    def "should be able to peek a number of element" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one);
        queue.enqueue(two);
        queue.enqueue(three);
        queue.enqueue(four);

        when:
        DataPeeks peeks = queue.peek(3)

        and:
        byte[] actualOne = queue.dequeue();

        then:
        assertThat(peeks).containsExactly(one, two, three)
        assertThat(actualOne).containsExactly(one)
    }

    def "Should be able to use a Peeks to skip all of them" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one);
        queue.enqueue(two);
        queue.enqueue(three);
        queue.enqueue(four);
        DataPeeks peeks = queue.peek(3)

        when:
        queue.skip(peeks)

        and:
        byte[] actualFour = queue.dequeue()

        then:
        assertThat(actualFour).containsExactly(four);
    }

    def "Should be able to use Peeks to skip even when things have been dequeued" () {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue queue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        queue.enqueue(one);
        queue.enqueue(two);
        queue.enqueue(three);
        queue.enqueue(four);
        DataPeeks peeks = queue.peek(3)

        when:
        byte[] actualOne = queue.dequeue()
        and:
        queue.skip(peeks)

        and:
        byte[] actualFour = queue.dequeue()

        then:
        assertThat(actualOne).containsExactly(one)
        assertThat(actualFour).containsExactly(four)
    }
}
