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
package org.tools4j.hoverraft.server;

import org.tools4j.hoverraft.config.ConfigBuilder;
import org.tools4j.hoverraft.config.ConsensusConfig;
import org.tools4j.hoverraft.config.ThreadingMode;
import org.tools4j.hoverraft.machine.StateMachine;
import org.tools4j.hoverraft.message.Message;
import org.tools4j.hoverraft.message.direct.DirectMessageFactory;
import org.tools4j.hoverraft.state.PersistentState;
import org.tools4j.hoverraft.state.ServerState;
import org.tools4j.hoverraft.state.VolatileState;
import org.tools4j.hoverraft.transport.Connections;
import org.tools4j.hoverraft.transport.MessageLog;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mockery {

    public static final int SERVER_ID = 1;
    public static final long MIN_ELECTION_TIMEOUT_MILLIS = 50;
    public static final long MAX_ELECTION_TIMEOUT_MILLIS = 100;

    public static Server direct(final int servers) {
        return direct(servers, 0, 0);
    }

    public static Server simple(final int servers) {
        return simple(servers, 0, 0);
    }

    public static Server direct(final int servers, final int sources, final int connections) {
        return server(servers, sources, connections, DirectMessageFactory.createForWriting());
    }

    public static Server simple(final int servers, final int sources, final int connections) {
        return server(servers, sources, connections, DirectMessageFactory.createForWriting());
    }

    private static Server server(final int servers, final int sources, final int connections,
                                 final DirectMessageFactory messageFactory) {
        final ConsensusConfig consensusConfig = consensusConfig(servers, sources);
        return new Server(SERVER_ID,
                consensusConfig(servers, sources), serverState(consensusConfig),
                messageLog(), stateMachine(), connections(servers, sources), messageFactory);
    }

    public static ConsensusConfig consensusConfig(final int servers, final int sources) {
        final ConfigBuilder configBuilder = new ConfigBuilder()
                .minElectionTimeoutMillis(MIN_ELECTION_TIMEOUT_MILLIS)
                .maxElectionTimeoutMillis(MAX_ELECTION_TIMEOUT_MILLIS)
                .threadingMode(ThreadingMode.SHARED);
        for (int i = 1; i <= servers; i++) {
            configBuilder.addServer(i, "server-" + i);
        }
        for (int i = 1; i <= sources; i++) {
            configBuilder.addSource(i, "source-" + i);
        }
        return configBuilder.build();
    }

    public static StateMachine stateMachine() {
        return mock(StateMachine.class);
    }

    public static <M extends Message> MessageLog<M> messageLog() {
        return mock(MessageLog.class);
    }

    public static <M extends Message> Connections<M> connections(final int servers, final int sources) {
        final Connections<M> connects = mock(Connections.class);
        //when
        return connects;
    }

    public static ServerState serverState(final ConsensusConfig consensusConfig) {
        final PersistentState persistentState = persistentState();
        return new ServerState(persistentState, new VolatileState(SERVER_ID, consensusConfig));
    }

    public static PersistentState persistentState() {
        final PersistentState persistentState = mock(PersistentState.class);
        when(persistentState.currentTerm()).thenReturn(1);
        when(persistentState.lastLogTerm()).thenReturn(0);
        when(persistentState.lastLogIndex()).thenReturn(-1L);
        when(persistentState.votedFor()).thenReturn(PersistentState.NOT_VOTED_YET);
        return persistentState;
    }
}