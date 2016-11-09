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
package org.tools4j.hoverraft.state;

import org.tools4j.hoverraft.handler.HigherTermHandler;
import org.tools4j.hoverraft.handler.MessageHandler;
import org.tools4j.hoverraft.handler.VoteRequestHandler;
import org.tools4j.hoverraft.message.AppendRequest;
import org.tools4j.hoverraft.message.Message;
import org.tools4j.hoverraft.message.TimeoutNow;
import org.tools4j.hoverraft.server.ServerContext;
import org.tools4j.hoverraft.util.Clock;

public class FollowerState extends AbstractState {

    private final MessageHandler messageHandler;

    public FollowerState(final PersistentState persistentState, final VolatileState volatileState) {
        super(Role.FOLLOWER, persistentState, volatileState);
        this.messageHandler = new HigherTermHandler(persistentState, volatileState)
                .thenHandleVoteRequest(new VoteRequestHandler(persistentState, volatileState)::onVoteRequest)
                .thenHandleAppendRequest(this::onAppendRequest)
                .thenHandleTimeoutNow(this::onTimeoutNow);
    }

    @Override
    public Role onMessage(final ServerContext serverContext, final Message message) {
        message.accept(serverContext, messageHandler);
        return volatileState().role();
    }

    @Override
    public void perform(ServerContext serverContext) {
        //nothing to do
    }

    private void onAppendRequest(final ServerContext serverContext, final AppendRequest appendRequest) {
        final int term = appendRequest.term();
        final int leaderId = appendRequest.leaderId();
        final int currentTerm = currentTerm();
        final boolean successful;
        if (currentTerm == term) /* should never be larger */ {
            volatileState().electionState().electionTimer().reset(Clock.DEFAULT);
            successful = appendToLog(serverContext, appendRequest);
        } else {
            successful = false;
        }
        serverContext.messageFactory().appendResponse()
                .term(currentTerm)
                .successful(successful)
                .sendTo(serverContext.connections().serverSender(leaderId),
                        serverContext.resendStrategy());
    }

    private boolean appendToLog(final ServerContext serverContext, final AppendRequest appendRequest) {
        //FIXME write to message log
        return true;
    }

    private void onTimeoutNow(final ServerContext serverContext, final TimeoutNow timeoutNow) {
        if (timeoutNow.term() == currentTerm() && timeoutNow.candidateId() == serverContext.id()) {
            volatileState().electionState().electionTimer().timeoutNow();
        }
    }
}
