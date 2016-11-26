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
import org.tools4j.hoverraft.command.Command;
import org.tools4j.hoverraft.direct.AbstractDirectPayload;

import java.nio.ByteBuffer;

public class DirectCommand extends AbstractDirectPayload implements Command {

    private static final int BYTE_LENGTH_OFF  = 0;
    private static final int BYTE_LENGTH_LEN  = 4;
    private static final int COMMAND_OFF      = BYTE_LENGTH_OFF + BYTE_LENGTH_LEN;

    @Override
    public int byteLength() {
        return readBuffer.getInt(offset + BYTE_LENGTH_OFF);
    }

    private Command byteLength(final int length) {
        writeBuffer.putInt(this.offset + BYTE_LENGTH_OFF, length);
        return this;
    }

    @Override
    public void bytesFrom(final byte[] bytes, final int offset, final int length) {
        byteLength(length);
        writeBuffer.putBytes(this.offset + COMMAND_OFF, bytes, offset, length);
    }

    @Override
    public void bytesFrom(final ByteBuffer bytes, final int offset, final int length) {
        byteLength(length);
        writeBuffer.putBytes(this.offset + COMMAND_OFF, bytes, offset, length);
    }

    @Override
    public void bytesFrom(final DirectBuffer bytes, final int offset, final int length) {
        byteLength(length);
        writeBuffer.putBytes(this.offset + COMMAND_OFF, bytes, offset, length);
    }

    @Override
    public void bytesTo(final byte[] bytes, final int offset) {
        readBuffer.getBytes(this.offset + COMMAND_OFF, bytes, offset, byteLength());
    }

    @Override
    public void bytesTo(final ByteBuffer bytes, final int offset) {
        readBuffer.getBytes(this.offset + COMMAND_OFF, bytes, offset, byteLength());
    }

    @Override
    public void bytesTo(final MutableDirectBuffer bytes, final int offset) {
        readBuffer.getBytes(this.offset + COMMAND_OFF, bytes, offset, byteLength());
    }

    @Override
    public void copyFrom(final Command command) {
        byteLength(command.byteLength());
        command.bytesTo(writeBuffer, this.offset + COMMAND_OFF);
    }
}
