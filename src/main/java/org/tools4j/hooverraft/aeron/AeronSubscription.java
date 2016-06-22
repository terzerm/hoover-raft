/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 hoover-raft (tools4j), Marco Terzer
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
package org.tools4j.hooverraft.aeron;

import io.aeron.logbuffer.FragmentHandler;
import org.tools4j.hooverraft.message.Subscription;

import java.util.Objects;

/**
 * Subscription from aeron channel/stream pair.
 */
public final class AeronSubscription implements Subscription {

    private final io.aeron.Subscription subscription;

    public AeronSubscription(final io.aeron.Subscription subscription) {
        this.subscription = Objects.requireNonNull(subscription);
    }

    @Override
    public int poll(final FragmentHandler fragmentHandler, final int fragmentLimit) {
        return subscription.poll(fragmentHandler, fragmentLimit);
    }
}
