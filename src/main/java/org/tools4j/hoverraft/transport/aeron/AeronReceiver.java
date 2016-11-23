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
package org.tools4j.hoverraft.transport.aeron;

import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import org.agrona.DirectBuffer;
import org.tools4j.hoverraft.direct.RecyclingDirectFactory;
import org.tools4j.hoverraft.message.Message;
import org.tools4j.hoverraft.message.MessageType;
import org.tools4j.hoverraft.transport.Receiver;

import java.util.Objects;
import java.util.function.Consumer;

public class AeronReceiver implements Receiver<Message> {

    private final RecyclingDirectFactory directFactory;
    private final Subscription subscription;
    private final AeronHandler aeronHandler = new AeronHandler();

    public AeronReceiver(final RecyclingDirectFactory directFactory, final Subscription subscription) {
        this.directFactory = Objects.requireNonNull(directFactory);
        this.subscription = Objects.requireNonNull(subscription);
    }

    @Override
    public int poll(final Consumer<? super Message> messageMandler, final int limit) {
        for (int i = 0; i < limit; i++) {
            if (0 < subscription.poll(aeronHandler, 1)) {
                messageMandler.accept(aeronHandler.message);
                aeronHandler.reset();
            } else {
                return i;
            }
        }
        return limit;
    }

    private class AeronHandler implements FragmentHandler {
        private Message message = null;

        @Override
        public void onFragment(final DirectBuffer buffer, final int offset, final int length, final Header header) {
            final MessageType messageType = MessageType.readFrom(buffer, offset);
            message = messageType.create(directFactory);
            message.wrap(buffer, offset);
        }

        public void reset() {
            if (message != null) {
                message.unwrap();
                message = null;
            }
        }
    }
}
