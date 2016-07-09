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
package org.tools4j.hoverraft.message;

import org.agrona.DirectBuffer;

public interface Publication {
    /**
     * The publication is not yet connected to a subscriber.
     */
    long NOT_CONNECTED = -1;

    /**
     * The offer failed due to back pressure from the subscribers preventing further transmission.
     */
    long BACK_PRESSURED = -2;

    /**
     * The offer failed due to an administration action and should be retried.
     */
    long ADMIN_ACTION = -3;

    /**
     * The {@link Publication} has been closed and should no longer be used.
     */
    long CLOSED = -4;

    /**
     * Non-blocking publish of a partial readBuffer containing a message.
     *
     * @param buffer containing message.
     * @param offset offset in the readBuffer at which the encoded message begins.
     * @param length in bytes of the encoded message.
     * @return The new stream position, otherwise a negative error value {@link #NOT_CONNECTED}, {@link #BACK_PRESSURED},
     * {@link #ADMIN_ACTION} or {@link #CLOSED}.
     */
    long offer(DirectBuffer buffer, int offset, int length);

    /**
     * Non-blocking publish of a full readBuffer containing a message.
     *
     * @param buffer containing message.
     * @return The new stream position, otherwise a negative error value {@link #NOT_CONNECTED}, {@link #BACK_PRESSURED},
     * {@link #ADMIN_ACTION} or {@link #CLOSED}.
     */
    default long offer(DirectBuffer buffer) {
        return offer(buffer, 0, buffer.capacity());
    }
}