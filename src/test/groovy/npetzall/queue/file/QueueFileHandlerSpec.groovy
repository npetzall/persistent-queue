package npetzall.queue.file

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.ByteBuffer
import java.nio.MappedByteBuffer

import static org.assertj.core.api.Assertions.assertThat

class QueueFileHandlerSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder();

    def "Can create a new QueueFile with correct size"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024

        when:
        new QueueFileHandler(queueFile, size)

        then:
        queueFile.length() == 1 * 1024
    }

    def "Newly created queueFile has writeOffset 0"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024

        when:
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        then:
        queueFileHandler.getWriteOffset() == 0
    }

    def "Newly created queueFile has readOffset 0"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024

        when:
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        then:
        queueFileHandler.getReadOffset() == 0
    }

    def "QueueFileHandler returns dataByteBuffer"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024

        when:
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        and:
        ByteBuffer dataByteBuffer = queueFileHandler.getDataByteBuffer()

        then:
        dataByteBuffer != null
        dataByteBuffer.limit() == 1024 - 12
    }

    def "QueueFileHandler can update writeOffset"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        int offset = 20
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        queueFileHandler.setWriteOffset(offset)

        and:
        int actual = queueFileHandler.getWriteOffset()

        then:
        actual == offset
    }

    def "QueueFileHandler can update readOffset"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        int offset = 20
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        queueFileHandler.setReadOffset(offset)

        and:
        int actual = queueFileHandler.getReadOffset()

        then:
        actual == offset
    }

    def "QueueFileHandler from existing contains writeOffset"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        int offset = 20
        new QueueFileHandler(queueFile, size).setWriteOffset(offset)
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        int actual = queueFileHandler.getWriteOffset();

        then:
        actual == offset
    }

    def "QueueFileHandler from existing contains readOffset"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        int offset = 20
        new QueueFileHandler(queueFile, size).setReadOffset(offset)
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        int actual = queueFileHandler.getReadOffset();

        then:
        actual == offset
    }

    def "should return size of data area and not file"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        int dataSize = queueFileHandler.getSize()

        then:
        dataSize == size - QueueFileHandler.DATA_OFFSET_POSITION
    }

    def "Create MappedByteBuffer" () {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        ByteBuffer byteBuffer = queueFileHandler.getDataByteBuffer()

        then:
        assertThat(byteBuffer).isInstanceOf(MappedByteBuffer)

    }
}
