package npetzall.queue.cache

import npetzall.queue.api.QueueFactory
import npetzall.queue.file.FileQueue
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

class ReadCacheFactoryFactorySpec extends Specification {

    def "offHeapQueueFactory"() {
        setup:
        QueueFactory queueFactory = ReadCacheFactoryFactory.offHeapFactory()
        FileQueue fileQueue = Mock {
            getSize() >> 1024
        }

        when:
        def queue = queueFactory.create(fileQueue)

        then:
        assertThat(queue).isInstanceOf(OffHeapReadCache.class)
    }

    def "onHeapQueueFactory"() {
        setup:
        QueueFactory queueFactory = ReadCacheFactoryFactory.onHeapFactory()
        FileQueue fileQueue = Mock {
            getSize() >> 1024
        }

        when:
        def queue = queueFactory.create(fileQueue)

        then:
        assertThat(queue).isInstanceOf(OnHeapReadCache.class);
    }

}
