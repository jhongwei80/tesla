package io.github.tesla.filter.support.servlet;

import java.io.IOException;

import javax.servlet.ServletOutputStream;

import io.netty.buffer.ByteBuf;

public class ByteBufferServletOutputStream extends ServletOutputStream {

    private final ByteBuf byteBuf;

    public ByteBufferServletOutputStream(ByteBuf byteBuf) {
        if (byteBuf == null) {
            throw new NullPointerException("buffer");
        }
        this.byteBuf = byteBuf;
    }

    @Override
    public void write(int b) throws IOException {
        byteBuf.writeByte((byte)b);
    }

}
