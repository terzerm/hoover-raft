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

import org.tools4j.hoverraft.event.EventHandler;
import org.tools4j.hoverraft.message.AppendResponse;
import org.tools4j.hoverraft.message.CommandMessage;
import org.tools4j.hoverraft.message.VoteRequest;
import org.tools4j.hoverraft.server.ServerContext;
import org.tools4j.hoverraft.timer.TimerEvent;

public class LeaderState extends AbstractState {

    public LeaderState(final PersistentState persistentState, final VolatileState volatileState) {
        super(Role.LEADER, persistentState, volatileState);
    }

    @Override
    protected EventHandler eventHandler() {
        return new EventHandler() {
            @Override
            public Transition onVoteRequest(final ServerContext serverContext, final VoteRequest voteRequest) {
                return LeaderState.this.onVoteRequest(serverContext, voteRequest);
            }

            @Override
            public Transition onAppendResponse(final ServerContext serverContext, final AppendResponse appendResponse) {
                return LeaderState.this.onAppendResponse(serverContext, appendResponse);
            }

            @Override
            public Transition onCommandMessage(final ServerContext serverContext, final CommandMessage commandMessage) {
                return LeaderState.this.onCommandMessage(serverContext, commandMessage);
            }

            @Override
            public Transition onTimerEvent(final ServerContext serverContext, final TimerEvent timerEvent) {
                return LeaderState.this.onTimerEvent(serverContext, timerEvent);
            }
        };
    }

    private Transition onCommandMessage(final ServerContext serverContext, final CommandMessage commandMessage) {
        serverContext.messageLog().append(commandMessage);
        sendAppendRequest(serverContext);//FIXME send command message in request
        return Transition.STEADY;
    }

    private Transition onAppendResponse(final ServerContext serverContext, final AppendResponse appendResponse) {
        //FIXME impl
        updateCommitIndex(serverContext);
        return Transition.STEADY;
    }

    private Transition onTimerEvent(final ServerContext serverContext, final TimerEvent timerEvent) {
        sendAppendRequest(serverContext);
        return Transition.STEADY;
    }

    private void updateCommitIndex(final ServerContext serverContext) {
        //FIXME impl
    }

    private void sendAppendRequest(final ServerContext serverContext) {
        //FIXME impl
    }

}
