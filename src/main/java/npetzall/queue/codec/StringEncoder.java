package npetzall.queue.codec;

import npetzall.queue.api.Encoder;

import java.nio.charset.Charset;

public class StringEncoder implements Encoder<String> {

    private final Charset charset;

    public StringEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public byte[] encode(String element) {
        return element.getBytes(charset);
    }
}
