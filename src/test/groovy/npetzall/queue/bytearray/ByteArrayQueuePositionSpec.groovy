package npetzall.queue.bytearray

import npetzall.queue.bytearray.ByteArrayQueue
import npetzall.queue.doubles.ByteBufferProviderDouble
import npetzall.queue.doubles.PositionHolderDouble
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class ByteArrayQueuePositionSpec extends Specification {

    @Shared byte[] one = "one".getBytes(StandardCharsets.UTF_8);

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "should update writeOffset when element is enqueued"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue fileQueue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)

        when:
        fileQueue.enqueue(one)

        then:
        0 * positionHolderDouble.readPosition(_)
        1 * positionHolderDouble.writePosition(_)

    }

    def "should update readOffset when element is dequeued"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue fileQueue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        fileQueue.enqueue(one)

        when:
        byte[] actualOne = fileQueue.dequeue()

        then:
        1 * positionHolderDouble.readPosition(_)
        0 * positionHolderDouble.writePosition(_)
        assertThat(actualOne).isEqualTo(one)

    }

    def "should be able to skip elements"() {
        setup:
        byte[] two = "two".getBytes(StandardCharsets.UTF_8)
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue fileQueue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        fileQueue.enqueue(one)
        fileQueue.enqueue(two)

        when:
        fileQueue.skip()

        then:
        1 * positionHolderDouble.readPosition(_)
        0 * positionHolderDouble.writePosition(_)
    }

    def "peek doesn't move readOffset"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue fileQueue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        fileQueue.enqueue(one)

        when:
        fileQueue.peek()

        then:
        0 * positionHolderDouble.writePosition(_)
        0 * positionHolderDouble.readPosition(_)
    }

    def "should be able to clear all elements"() {
        setup:
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(1024))
        PositionHolderDouble positionHolderDouble = Spy(PositionHolderDouble)
        ByteArrayQueue fileQueue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)
        fileQueue.enqueue(one)

        when:
        fileQueue.clear()

        then:
        1 * positionHolderDouble.writePosition(0)
        1 * positionHolderDouble.readPosition(0)
    }

}
