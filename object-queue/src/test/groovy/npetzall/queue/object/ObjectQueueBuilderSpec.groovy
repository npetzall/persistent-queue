package npetzall.queue.object

import npetzall.queue.api.Queue
import npetzall.queue.object.codec.StringTranscoder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ObjectQueueBuilderSpec extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    def "Build a queue without read cache"() {
        when:
        Queue<String> queue = ObjectQueueBuilder
                .queueFile(temporaryFolder.newFile("queueWithReadThroughReadCache"))
                .size("20m")
                .transcoder(new StringTranscoder())
                .build();
        then:
        queue != null
    }
}
