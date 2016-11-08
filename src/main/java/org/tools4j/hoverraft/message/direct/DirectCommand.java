/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hover-raft (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.tools4j.hoverraft.message.direct;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.tools4j.hoverraft.machine.Command;

import java.nio.ByteBuffer;

abstract public class DirectCommand implements Command {

    protected static final int BYTE_LENGTH_OFF  = 0;
    protected static final int BYTE_LENGTH_LEN  = 4;
    protected static final int COMMAND_OFF      = BYTE_LENGTH_OFF + BYTE_LENGTH_LEN;

    abstract protected DirectBuffer readBuffer();

    abstract protected MutableDirectBuffer writeBuffer();

    abstract protected int offset();

    public int byteLength() {
        return readBuffer().getInt(offset());
    }

    private Command byteLength(final int length) {
        writeBuffer().putInt(offset() + BYTE_LENGTH_OFF, length);
        return this;
    }

    @Override
    public void bytesFrom(final byte[] bytes, final int offset, final int length) {
        byteLength(length);
        writeBuffer().putBytes(offset() + COMMAND_OFF, bytes, offset, length);
    }

    @Override
    public void bytesFrom(final ByteBuffer bytes, final int offset, final int length) {
        byteLength(length);
        writeBuffer().putBytes(offset() + COMMAND_OFF, bytes, offset, length);
    }

    @Override
    public void bytesFrom(final DirectBuffer bytes, final int offset, final int length) {
        byteLength(length);
        writeBuffer().putBytes(offset() + COMMAND_OFF, bytes, offset, length);
    }

    @Override
    public void bytesTo(final byte[] bytes, final int offset) {
        readBuffer().getBytes(offset() + COMMAND_OFF, bytes, offset, byteLength());
    }

    @Override
    public void bytesTo(final ByteBuffer bytes, final int offset) {
        readBuffer().getBytes(offset() + COMMAND_OFF, bytes, offset, byteLength());
    }

    @Override
    public void bytesTo(final MutableDirectBuffer bytes, final int offset) {
        readBuffer().getBytes(offset() + COMMAND_OFF, bytes, offset, byteLength());
    }

}
