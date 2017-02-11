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
        queueFileHandler.writePosition() == 0
    }

    def "Newly created queueFile has readOffset 0"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024

        when:
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        then:
        queueFileHandler.readPosition() == 0
    }

    def "QueueFileHandler returns dataByteBuffer"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024

        when:
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        and:
        ByteBuffer dataByteBuffer = queueFileHandler.byteBuffer()

        then:
        dataByteBuffer != null
        dataByteBuffer.limit() == 1024 - 8
    }

    def "QueueFileHandler can update writeOffset"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        int offset = 20
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        queueFileHandler.writePosition(offset)

        and:
        int actual = queueFileHandler.writePosition()

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
        queueFileHandler.readPosition(offset)

        and:
        int actual = queueFileHandler.readPosition()

        then:
        actual == offset
    }

    def "QueueFileHandler from existing contains writeOffset"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        int offset = 20
        new QueueFileHandler(queueFile, size).writePosition(offset)
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        int actual = queueFileHandler.writePosition();

        then:
        actual == offset
    }

    def "QueueFileHandler from existing contains readOffset"() {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        int offset = 20
        new QueueFileHandler(queueFile, size).readPosition(offset)
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        int actual = queueFileHandler.readPosition();

        then:
        actual == offset
    }

    def "Create MappedByteBuffer" () {
        setup:
        File queueFile = temporaryFolder.newFile("newQueueFile")
        int size = 1 * 1024
        QueueFileHandler queueFileHandler = new QueueFileHandler(queueFile, size)

        when:
        ByteBuffer byteBuffer = queueFileHandler.byteBuffer()

        then:
        assertThat(byteBuffer).isInstanceOf(MappedByteBuffer)

    }
}
