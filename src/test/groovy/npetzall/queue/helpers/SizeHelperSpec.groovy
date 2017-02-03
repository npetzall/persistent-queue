package npetzall.queue.helpers

import spock.lang.Specification

class SizeHelperSpec extends Specification {

    def "no unit returns bytes"() {
        when:
        int size = SizeHelper.parse("20")
        then:
        size == 20
    }

    def "k as unit returns kilobytes"() {
        when:
        int size = SizeHelper.parse("20k")
        then:
        size == (20 * 1024)
    }

    def "m as unit returns megabytes"() {
        when:
        int size = SizeHelper.parse("20m")
        then:
        size == (20 * 1024 * 1024)
    }

    def "g as unit returns gigbytes"() {
        when:
        int size = SizeHelper.parse("20g")
        then:
        size == (20 * 1024 * 1024 * 1024)
    }
}
