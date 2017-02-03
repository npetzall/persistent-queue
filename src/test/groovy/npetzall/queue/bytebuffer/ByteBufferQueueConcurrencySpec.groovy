package npetzall.queue.bytebuffer

import org.assertj.core.extractor.Extractors
import spock.lang.Specification
import spock.lang.Timeout

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

import static org.assertj.core.api.Assertions.assertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy

class ByteBufferQueueConcurrencySpec extends Specification {


    @Timeout(5)
    def "Concurrent writes and reads"() {
        setup:
        List<String> toWrite = ["one", "two", "three", "four", "five", "six"]
        ArrayList<String> beenRead = [];
        ByteBufferQueue byteBufferQueue = new ByteBufferQueue(ByteBuffer.allocate(2048))

        when:
        Thread.start {
            toWrite.each {
                byteBufferQueue.enqueue(asBytes(it))
                sleep(100)
            }
        }
        Thread.start {
            while (beenRead.size() < toWrite.size()) {
                byte[] data = byteBufferQueue.dequeue()
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
