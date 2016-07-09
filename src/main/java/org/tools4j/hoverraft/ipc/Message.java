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
package org.tools4j.hoverraft.ipc;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.tools4j.hoverraft.message.Publication;

import java.util.Objects;

/**
 * A message
 */
public class Message {

    private final int messageSize;

    protected DirectBuffer readBuffer;
    protected MutableDirectBuffer writeBuffer;
    protected int offset;

    public Message(final int messageSize) {
        this.messageSize = messageSize;
    }

    public void wrap(final DirectBuffer buffer, final int offset, final int length) {
        if (length < messageSize) throw new IllegalArgumentException("length is too small: " + length + " < " + messageSize);
        wrap(buffer, offset);
    }

    public void wrap(final DirectBuffer buffer, final int offset) {
        this.readBuffer = Objects.requireNonNull(buffer);
        if (buffer instanceof MutableDirectBuffer) {
            this.writeBuffer = (MutableDirectBuffer)buffer;
        } else {
            this.writeBuffer = null;
        }
        this.offset = offset;
    }


    public long offerTo(final Publication publication) {
        return publication.offer(readBuffer, offset, messageSize);
    }

    public long offerTo(final Publication publication, final int maxTries) {
        long lastResult = Long.MIN_VALUE;
        int tries = maxTries;
        while (tries > 0) {
            tries--;
            final long result = offerTo(publication);
            if (result >= 0) {
                return result;
            }
            if (result != Publication.BACK_PRESSURED && result != Publication.ADMIN_ACTION) {
                return result;
            }
            lastResult = result;
        };
        return lastResult;
    }
}