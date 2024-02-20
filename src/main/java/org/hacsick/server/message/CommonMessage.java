package org.hacsick.server.message;

import java.io.Serializable;

public class CommonMessage implements Message, Serializable {
    private final String from;
    private final String to;
    private final String message;

    public static CommonMessage of(final String from, final String to, final String message) {
        return new CommonMessage(from, to, message);
    }

    public static CommonMessage ofAdmin(final String to, final String message) {
        return new CommonMessage(ServerMeta.SERVER.getValue(), to, message);
    }

    public static CommonMessage toAdmin(final String from, final String message) {
        return new CommonMessage(from, ServerMeta.SERVER.getValue(), message);
    }

    protected CommonMessage(final String from, final String to, final String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChatRoomMessage{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
