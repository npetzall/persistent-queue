package npetzall.queue.codec;

import npetzall.queue.api.Decoder;

import java.nio.charset.Charset;

public class StringDecoder implements Decoder<String> {

    private final Charset charset;

    public StringDecoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String decode(byte[] data) {
        return new String(data, charset);
    }
}
