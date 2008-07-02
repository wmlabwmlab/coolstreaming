package se.peertv.peertvsim.examples.Tennis;

import se.peertv.peertvsim.network.Message;
import simpipe.examples.TennisPlayer;

//Adapted from Mina VMPipe Example


public class TennisBall extends Message{
	private final boolean ping;

    private final int ttl;

    /**
     * Creates a new ball with the specified TTL (Time To Live) value.
     */
    public TennisBall(int ttl) {
        this(ttl, true);
    }

    /**
     * Creates a new ball with the specified TTL value and PING/PONG state.
     */
    private TennisBall(int ttl, boolean ping) {
        this.ttl = ttl;
        this.ping = ping;
    }

    /**
     * Returns the TTL value of this ball.
     */
    public int getTTL() {
        return ttl;
    }

    /**
     * Returns the ball after {@link TennisPlayer}'s stroke.
     * The returned ball has decreased TTL value and switched PING/PONG state.
     */
    public TennisBall stroke() {
        return new TennisBall(ttl - 1, !ping);
    }

    /**
     * Returns string representation of this message (<code>[PING|PONG]
     * (TTL)</code>).
     */
    public String toString() {
        if (ping) {
            return "PING (" + ttl + ")";
        } else {
            return "PONG (" + ttl + ")";
        }
    }
}