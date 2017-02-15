package npetzall.queue.bytearray

import npetzall.queue.doubles.ByteBufferProviderDouble
import npetzall.queue.doubles.PositionHolderDouble
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Timeout

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat

class ByteArrayQueueConcurrencySpec extends Specification {

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Timeout(5)
    def "Concurrent writes and reads"() {
        setup:
        List<String> toWrite = ["one", "two", "three", "four", "five", "six"]
        ArrayList<String> beenRead = [];
        ByteBufferProviderDouble byteBufferProviderDouble = new ByteBufferProviderDouble(ByteBuffer.allocate(2040))
        PositionHolderDouble positionHolderDouble = new PositionHolderDouble()
        ByteArrayQueue byteBufferQueue = new ByteArrayQueue(byteBufferProviderDouble, positionHolderDouble)

        when:
        Thread.start {
            toWrite.each {
                byteBufferQueue.enqueue(asBytes(it))
                sleep(100)
            }
        }
        Thread.start {
            while (beenRead.size() < toWrite.size()) {
                byte[] data = byteBufferQueue.deque()
                if (data != null && data.length > 0) {
                    beenRead.add(asString(data))
                }
            }
        }
        while(beenRead.size() < toWrite.size()) {
            sleep(100)
        }

        then:
        assertThat(beenRead.size()).isEqualTo(toWrite.size())
        assertThat(beenRead).containsExactly(toWrite.toArray())
    }

    private byte[] asBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8)
    }

    private String asString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8)
    }
}
